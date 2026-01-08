package com.example.unscramble.viewModel
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.unscramble.Components.UserPreferencesRepository
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


enum class GameSound{
    CORRECT_GUESS,
    WRONG_GUESS,
    GAME_OVER,
}

class GameViewModel(
    private val userPreferencesRepository: UserPreferencesRepository

) : ViewModel(){

    private val _soundEvent = MutableSharedFlow<GameSound>()
    val soundEvent: SharedFlow<GameSound> = _soundEvent.asSharedFlow()

    private  val _uiState = MutableStateFlow(GameUiState()) //realdata which is only changed by view model
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()   //exposed to UI for read-Only purposes
    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")

    init {
        // Observe high score and update UI state whenever it changes in DataStore
        viewModelScope.launch {
            userPreferencesRepository.highestScore.collect { hScore ->
                _uiState.update { it.copy(highestScore = hScore) }
            }
        }
        resetGame()
    }

    private fun triggerSound(sound : GameSound){
        viewModelScope.launch {
            _soundEvent.emit(sound)
        }
    }

    fun selectDifficulty(selectedDifficulty: Difficulty){
        _uiState.update { currentState ->
            currentState.copy(difficulty = selectedDifficulty)
        }
        resetGame()
    }

    private fun pickRandomWordAndShuffle() : String {
        val difficulty = _uiState.value.difficulty
        val wordList = allWords.filter{word ->
            when(difficulty){
                 Difficulty.EASY -> word.length <= 5
                 Difficulty.MEDIUM -> word.length in 6..8
                 Difficulty.HARD -> word.length > 8
            }
        }
        do{
            currentWord = wordList.random()
        }while(usedWords.contains(currentWord))

        usedWords.add(currentWord)
        return shuffleCurrentWord(currentWord)
    }

    private fun shuffleCurrentWord(word: String): String{
        val tempWord = word.toCharArray() //convert a String into Array of charaters
        tempWord.shuffle()
        while(String(tempWord) == word){
            tempWord.shuffle()
        }
        return String(tempWord) //return the charArray as a String
    }

    fun resetGame(){
       usedWords.clear()
       _uiState.value = GameUiState(
           currentScrambledWord = pickRandomWordAndShuffle(),
           difficulty = Difficulty.EASY
       )
    }

    fun updateUserGuess(guessWord: String){
         userGuess = guessWord
    }

    fun checkUserGuess(){
      if(userGuess.equals(currentWord, ignoreCase = true)){
          val updateScore = _uiState.value.score.plus(SCORE_INCREASE)
          updateGameState(updateScore)
          triggerSound(GameSound.CORRECT_GUESS)
      }else{
        _uiState.update { currentState ->
            currentState.copy(isGuessedWordWrong = true)
        }
          triggerSound(GameSound.WRONG_GUESS)
      }
        updateUserGuess("") //blank string pass krdi
    }

    fun updateGameState(updatedScore: Int){
        if(usedWords.size == MAX_NO_OF_WORDS){
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true,
                )
            }
            // Check and save high score when game ends
            viewModelScope.launch {
                userPreferencesRepository.saveHighestScore(updatedScore)
            }
            triggerSound(GameSound.GAME_OVER)
        }else{
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                ) }
        }

    }

    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }
}
