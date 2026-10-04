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
import androidx.compose.material3.CircularProgressIndicator
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
fun RecoveryScreen(
    securityQuestion: String?,
    onVerifyAnswer: (answer: String, onResult: (Boolean) -> Unit) -> Unit,
    onNewPatternSet: (String) -> Unit,
    onBackClick: () -> Unit // Action to return to unlock screen
) {
    var isAnswerValid by remember { mutableStateOf(false) }
    var answerInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // States for double pattern verification
    var firstPattern by remember { mutableStateOf("") }
    var confirmPattern by remember { mutableStateOf("") }
    var patternKey by remember { mutableStateOf(0) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isAnswerValid) {
            // Step 1: Answer security question
            Text(
                text = "Recuperar Acceso",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (securityQuestion == null) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = securityQuestion,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = answerInput,
                onValueChange = {
                    answerInput = it
                    errorMessage = null
                },
                label = { Text("Tu respuesta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (answerInput.isNotBlank()) {
                        isLoading = true
                        onVerifyAnswer(answerInput) { isValid ->
                            isLoading = false
                            if (isValid) {
                                isAnswerValid = true
                                errorMessage = null
                            } else {
                                errorMessage = "Respuesta incorrecta. Inténtalo de nuevo."
                            }
                        }
                    } else {
                        errorMessage = "Ingresa tu respuesta."
                    }
                },
                enabled = !isLoading && securityQuestion != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Verificando..." else "Verificar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Button to return to pattern screen
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar y volver")
            }

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        } else {
            // Step 2: Set and confirm new pattern
            val titleText = if (firstPattern.isEmpty()) {
                "Dibuja tu nuevo patrón"
            } else {
                "Confirma tu nuevo patrón"
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
                        patternKey++
                    } else {
                        confirmPattern = drawnPattern
                        if (firstPattern == confirmPattern) {
                            errorMessage = null
                            showConfirmationDialog = true
                        } else {
                            errorMessage = "Los patrones no coinciden. Inténtalo de nuevo."
                            firstPattern = ""
                            confirmPattern = ""
                            patternKey++
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
                    patternKey++
                }
            ) {
                Text("Reiniciar trazo")
            }
        }
    }

    // Confirmation dialog for the new pattern
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar Nuevo Patrón") },
            text = { Text("¿Estás segura de que deseas guardar este nuevo patrón de desbloqueo?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        onNewPatternSet(firstPattern)
                    }
                ) {
                    Text("Sí, cambiar patrón")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        firstPattern = ""
                        confirmPattern = ""
                        patternKey++
                    }
                ) {
                    Text("Volver a dibujar")
                }
            }
        )
    }
}