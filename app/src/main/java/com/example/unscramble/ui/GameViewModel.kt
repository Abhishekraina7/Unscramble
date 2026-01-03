package com.example.unscramble.ui
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class GameViewModel : ViewModel(){
    private  val _uiState = MutableStateFlow(GameUiState()) //realdata which is only changed by view model
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()   //exposed to UI for read-Only purposes
    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    init {
        resetGame()
    }

    private fun pickRandomWordAndShuffle() : String {
        currentWord = allWords.random() //pick random word from the allWords Set of Strings
        if(usedWords.contains(currentWord)){
            return pickRandomWordAndShuffle()
        }else{
            usedWords.add(currentWord)
            return pickRandomWordAndShuffle()
        }
    }

    private fun shuffleCurrentWord(word: String): String{
        val tempWord = word.toCharArray() //convert a String into Array of charaters
        tempWord.shuffle()
        while(String(tempWord) == word){
            tempWord.shuffle()
        }
        return String(tempWord) //return the charArray as a String
    }

    private fun resetGame(){
       usedWords.clear()
       _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }
}
