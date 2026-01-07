package com.example.unscramble.ui

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
}

data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false,
    val difficulty: Difficulty = Difficulty.EASY
)

