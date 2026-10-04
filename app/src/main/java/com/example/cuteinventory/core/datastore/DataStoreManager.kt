package com.example.cuteinventory.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        private val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
        private val SECURITY_PATTERN = stringPreferencesKey("security_pattern")
        private val SECURITY_QUESTION = stringPreferencesKey("security_question")
        private val SECURITY_ANSWER_HASH = stringPreferencesKey("security_answer_hash")
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }

    // --- First time launch ---
    val isFirstTime: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_FIRST_TIME] ?: true
    }

    suspend fun setFirstTimeCompleted() {
        context.dataStore.edit { prefs ->
            prefs[IS_FIRST_TIME] = false
        }
    }

    // --- Security Pattern ---
    val savedPattern: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SECURITY_PATTERN]
    }

    suspend fun savePattern(pattern: String) {
        context.dataStore.edit { prefs ->
            prefs[SECURITY_PATTERN] = pattern
        }
    }

    // --- Recovery Question & Answer ---
    val securityQuestion: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SECURITY_QUESTION]
    }

    suspend fun saveSecurityData(question: String, answer: String) {
        context.dataStore.edit { prefs ->
            prefs[SECURITY_QUESTION] = question
            prefs[SECURITY_ANSWER_HASH] = hashString(answer.trim().lowercase())
        }
    }

    // Verify hashed security answer
    suspend fun verifyAnswer(answer: String): Boolean {
        val prefs = context.dataStore.data.first()
        val savedHash = prefs[SECURITY_ANSWER_HASH] ?: return false
        val inputHash = hashString(answer.trim().lowercase())
        return savedHash == inputHash
    }

    // --- Theme Preference ---
    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[IS_DARK_MODE]
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_DARK_MODE] = enabled
        }
    }

    // SHA-256 hash helper for recovery answer
    private fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}