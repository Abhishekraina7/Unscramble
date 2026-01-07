package com.example.unscramble.ui
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.unscramble.R

/**
 * A helper class to manage loading and playing sounds using SoundPool.
 */
class SoundPlayer(context: Context) {

    // Sound IDs - will be populated when sounds are loaded
    private var correctGuessSoundId: Int = 0
    private var wrongGuessSoundId: Int = 0
    private var gameOverSoundId: Int = 0

    private val soundPool: SoundPool

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3) // Can play up to 3 sounds at once
            .setAudioAttributes(audioAttributes)
            .build()

        // Load the sounds and store their IDs
        // The last parameter (priority) is 1, which is the default.
        correctGuessSoundId = soundPool.load(context, R.raw.correct_guess, 1)
        wrongGuessSoundId = soundPool.load(context, R.raw.wrong_guess, 1)
        gameOverSoundId = soundPool.load(context, R.raw.game_over, 1)
    }

    fun playCorrectGuessSound() {
        soundPool.play(correctGuessSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playWrongGuessSound() {
        soundPool.play(wrongGuessSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playGameOverSound() {
        soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f)
    }

    /**
     * Call this from the Activity's onDestroy to release resources.
     */
    fun release() {
        soundPool.release()
    }
}
