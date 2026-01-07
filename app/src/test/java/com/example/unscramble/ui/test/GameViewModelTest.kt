package com.example.unscramble.ui.test
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertNotSame
import org.junit.Test

class GameViewModelTest {
    private var viewModel = GameViewModel()

    //It is similar to using static in java
    companion object {
        private const val SCORE_AFTER_CORRECT_ANSWER  = SCORE_INCREASE
    }

    //success path test
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset()  {
        val currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        val updatedState = viewModel.uiState.value
        assertFalse(updatedState.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_CORRECT_ANSWER, updatedState.score)
    }

    //error path test
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet(){
        val inCorrectPlayerWord = "and"
        viewModel.updateUserGuess(inCorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value
        assertEquals(0, currentGameUiState.score)
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded(){
        val gameUiState = viewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        val updatedGameUiState = viewModel.uiState.value
        assertNotSame(unScrambledWord, updatedGameUiState.currentScrambledWord)
        assertEquals(1,updatedGameUiState.currentWordCount)
        assertEquals(0, updatedGameUiState.score)
        assertFalse(updatedGameUiState.isGuessedWordWrong)
        assertFalse(updatedGameUiState.isGameOver)
    }

    @Test
    fun gameViewModel_AllWordGuessed_UIStateUpdatedCorrectly(){
        var expectedScore = 0
        var gameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        repeat(MAX_NO_OF_WORDS){
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()

            gameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(gameUiState.currentScrambledWord)
            assertEquals(expectedScore, gameUiState.score)
        }

        assertEquals(MAX_NO_OF_WORDS, gameUiState.currentWordCount)
        assertTrue(gameUiState.isGameOver)
    }
}