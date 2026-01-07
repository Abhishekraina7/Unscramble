package com.example.unscramble.ui
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import kotlinx.coroutines.flow.update


class GameViewModel : ViewModel(){
    private  val _uiState = MutableStateFlow(GameUiState()) //realdata which is only changed by view model
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()   //exposed to UI for read-Only purposes
    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")

    init {
        resetGame()
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
      }else{
        _uiState.update { currentState ->
            currentState.copy(isGuessedWordWrong = true)
        }
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
