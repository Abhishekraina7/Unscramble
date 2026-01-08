package com.example.unscramble.Components

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(val context: Context) {
    private val HIGHEST_SCORE = intPreferencesKey("highest_score")

    // read data
    val highestScore: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGHEST_SCORE] ?: 0
    }

    //write data
    suspend fun saveHighestScore(score: Int){
        context.dataStore.edit { preferences ->
            val currentHigh = preferences[HIGHEST_SCORE] ?:0
            if(score > currentHigh){
                preferences[HIGHEST_SCORE]= score
            }
        }
    }
}