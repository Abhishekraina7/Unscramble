package com.example.unscramble.ui.test
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class GameViewModelTest {
    private lateinit var viewModel: GameViewModel

    @Before
    fun setup(){
        viewModel = GameViewModel()
    }

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


}