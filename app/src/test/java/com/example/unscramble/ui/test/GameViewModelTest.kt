package com.example.unscramble.ui.test

import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameUiState
import com.example.unscramble.ui.GameViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.flow.StateFlow
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset()  {
        var uiState: StateFlow<GameUiState> = viewModel.uiState
        val currentGameUiState = uiState
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.value.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        assertFalse(currentGameUiState.value.isGuessedWordWrong)
        assertEquals(SCORE_AFTER_CORRECT_ANSWER, currentGameUiState.value.score)
    }

    //It is similar to using static in java
    companion object {
        private const val SCORE_AFTER_CORRECT_ANSWER  = SCORE_INCREASE
    }

}