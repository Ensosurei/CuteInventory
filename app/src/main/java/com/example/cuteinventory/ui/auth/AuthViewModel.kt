package com.example.cuteinventory.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuteinventory.core.datastore.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Represents the navigation state during auth flow
sealed interface AuthUiState {
    object Loading : AuthUiState
    object FirstTimeSetup : AuthUiState
    object UnlockRequired : AuthUiState
    object Authenticated : AuthUiState
}

class AuthViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _securityQuestion = MutableStateFlow<String?>(null)
    val securityQuestion: StateFlow<String?> = _securityQuestion.asStateFlow()

    val isDarkMode = dataStoreManager.isDarkMode

    init {
        checkInitialState()
    }

    // Determine whether to show setup or unlock screen
    private fun checkInitialState() {
        viewModelScope.launch {
            val isFirstTime = dataStoreManager.isFirstTime.first()
            if (isFirstTime) {
                _uiState.value = AuthUiState.FirstTimeSetup
            } else {
                _securityQuestion.value = dataStoreManager.securityQuestion.first()
                _uiState.value = AuthUiState.UnlockRequired
            }
        }
    }

    // Save initial pattern and security question
    fun completeFirstTimeSetup(pattern: String, question: String, answer: String) {
        viewModelScope.launch {
            dataStoreManager.savePattern(pattern)
            dataStoreManager.saveSecurityData(question, answer)
            dataStoreManager.setFirstTimeCompleted()
            _uiState.value = AuthUiState.Authenticated
        }
    }

    // Verify pattern entered on unlock screen
    fun verifyPattern(enteredPattern: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val saved = dataStoreManager.savedPattern.first()
            val isValid = saved == enteredPattern
            if (isValid) {
                _uiState.value = AuthUiState.Authenticated
            }
            onResult(isValid)
        }
    }

    fun loadSecurityQuestion() {
        viewModelScope.launch {
            _securityQuestion.value = dataStoreManager.securityQuestion.first()
        }
    }
    // Verify answer for pattern recovery
    fun verifyRecoveryAnswer(answer: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isValid = dataStoreManager.verifyAnswer(answer)
            onResult(isValid)
        }
    }

    // Reset pattern after successful recovery
    fun resetPattern(newPattern: String) {
        viewModelScope.launch {
            dataStoreManager.savePattern(newPattern)
            _uiState.value = AuthUiState.Authenticated
        }
    }

    // Toggle app light/dark theme
    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setDarkMode(enabled)
        }
    }
}