package com.example.cuteinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cuteinventory.core.datastore.DataStoreManager
import com.example.cuteinventory.core.theme.CuteInventoryTheme
import com.example.cuteinventory.ui.auth.AuthUiState
import com.example.cuteinventory.ui.auth.AuthViewModel
import com.example.cuteinventory.ui.auth.OnboardingScreen
import com.example.cuteinventory.ui.auth.RecoveryScreen
import com.example.cuteinventory.ui.auth.UnlockScreen

class MainActivity : ComponentActivity() {

    // Factory to inject DataStoreManager into AuthViewModel
    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val dataStoreManager = DataStoreManager(applicationContext)
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(dataStoreManager) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CuteInventoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by authViewModel.uiState.collectAsState()
                    val securityQuestion by authViewModel.securityQuestion.collectAsState()
                    var isRecovering by remember { mutableStateOf(false) }

                    when (uiState) {
                        is AuthUiState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is AuthUiState.FirstTimeSetup -> {
                            OnboardingScreen(
                                onComplete = { pattern, question, answer ->
                                    authViewModel.completeFirstTimeSetup(pattern, question, answer)
                                }
                            )
                        }

                        is AuthUiState.UnlockRequired -> {
                            if (isRecovering) {
                                RecoveryScreen(
                                    securityQuestion = securityQuestion,
                                    onVerifyAnswer = { answer, onResult ->
                                        authViewModel.verifyRecoveryAnswer(answer, onResult)
                                    },
                                    onNewPatternSet = { newPattern ->
                                        authViewModel.resetPattern(newPattern)
                                        isRecovering = false
                                    },
                                    onBackClick = {
                                        isRecovering = false // Return to unlock screen
                                    }
                                )
                            } else {
                                UnlockScreen(
                                    onPatternEntered = { pattern, onResult ->
                                        authViewModel.verifyPattern(pattern, onResult)
                                    },
                                    onForgotPasswordClick = {
                                        authViewModel.loadSecurityQuestion()
                                        isRecovering = true
                                    }
                                )
                            }
                        }

                        is AuthUiState.Authenticated -> {
                            InventoryMainScreen()
                        }
                    }
                }
            }
        }
    }
}

// Temporary placeholder for the main inventory screen
@Composable
fun InventoryMainScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¡Bienvenida al Inventario!",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}