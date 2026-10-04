package com.example.cuteinventory.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cuteinventory.core.components.PatternLockView

@Composable
fun OnboardingScreen(
    onComplete: (pattern: String, question: String, answer: String) -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Pattern creation state
    var firstPattern by remember { mutableStateOf("") }
    var confirmPattern by remember { mutableStateOf("") }
    var patternKey by remember { mutableStateOf(0) }
    var savedPattern by remember { mutableStateOf("") }

    // Security question state
    var questionInput by remember { mutableStateOf("") }
    var answerInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dialog state for pattern confirmation
    var showPatternDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (step == 1) {
            // Step 1: Set and Confirm Pattern
            val titleText = if (firstPattern.isEmpty()) {
                "Crea tu patrón de desbloqueo"
            } else {
                "Confirma tu patrón de desbloqueo"
            }

            Text(
                text = titleText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            PatternLockView(
                clearTrigger = patternKey,
                onPatternComplete = { drawnPattern ->
                    if (firstPattern.isEmpty()) {
                        firstPattern = drawnPattern
                        errorMessage = null
                        patternKey++ // Clear canvas when moving to confirmation
                    } else {
                        confirmPattern = drawnPattern
                        if (firstPattern == confirmPattern) {
                            savedPattern = firstPattern
                            errorMessage = null
                            showPatternDialog = true // Trigger confirmation dialog
                        } else {
                            errorMessage = "Los patrones no coinciden. Inténtalo de nuevo."
                            firstPattern = ""
                            confirmPattern = ""
                            patternKey++ // Clear canvas on pattern mismatch
                        }
                    }
                }
            )

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    firstPattern = ""
                    confirmPattern = ""
                    errorMessage = null
                    patternKey++ // Clear canvas on manual reset
                }
            ) {
                Text("Reiniciar trazo")
            }

        } else {
            // Step 2: Configure Security Question
            Text(
                text = "Pregunta de Seguridad",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = questionInput,
                onValueChange = {
                    questionInput = it
                    errorMessage = null
                },
                label = { Text("Escribe tu pregunta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = answerInput,
                onValueChange = {
                    answerInput = it
                    errorMessage = null
                },
                label = { Text("Escribe tu respuesta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (questionInput.isNotBlank() && answerInput.isNotBlank()) {
                        onComplete(savedPattern, questionInput.trim(), answerInput.trim())
                    } else {
                        errorMessage = "Por favor completa ambos campos."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Configuración")
            }

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    // Confirmation dialog for pattern setup
    if (showPatternDialog) {
        AlertDialog(
            onDismissRequest = { showPatternDialog = false },
            title = { Text("Confirmar Patrón") },
            text = { Text("¿Estás segura de que deseas usar este patrón de desbloqueo?") },
            confirmButton = {
                Button(
                    onClick = {
                        showPatternDialog = false
                        step = 2 // Advance to security question
                    }
                ) {
                    Text("Sí, usar patrón")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPatternDialog = false
                        firstPattern = ""
                        confirmPattern = ""
                        patternKey++ // Clear canvas to draw again
                    }
                ) {
                    Text("Volver a dibujar")
                }
            }
        )
    }
}