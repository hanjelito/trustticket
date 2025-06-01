package es.polizia.trustticket.ui.screen.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.polizia.trustticket.R
import es.polizia.trustticket.ui.viewModel.AuthState
import es.polizia.trustticket.ui.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (auth_jwt: String) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    // 1) Observamos el estado de login del ViewModel
    val loginState by authViewModel.loginState.collectAsState()

    // 2) Estados locales para TextField
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 3) SnackbarHostState para mostrar errores
    val snackbarHostState = remember { SnackbarHostState() }

    // 4) Efecto: si hay éxito o error, actuamos
    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthState.Success -> {
                val token = (loginState as AuthState.Success).token
                onLoginSuccess(token)
            }
            is AuthState.Error -> {
                val msg = (loginState as AuthState.Error).message
                snackbarHostState.showSnackbar(msg)
            }
            else -> { /* Idle o Loading: no hacemos nada */ }
        }
    }

    // ----------------------------------
    // 5) UI: Fondo con degradado + tarjeta centrada
    // ----------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
    ) {
        // 5.1) Tarjeta elevada (Material3 ElevatedCard)
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .align(Alignment.Center)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 5.1.1) Logo centrado
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                )

                // 5.1.2) Título “Bienvenido”
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // 5.1.3) Campo de Usuario (OutlinedTextField)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = {
                        Text(
                            text = "Usuario",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Icono de usuario",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        // Borde en azul cuando está enfocado
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        // Borde en gris cuando no está enfocado
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        // (Opcional) otras variantes:
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 5.1.4) Campo de Contraseña (OutlinedTextField)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = "Contraseña",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Icono de contraseña",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        val visibilityIcon =
                            if (passwordVisible) R.drawable.ic_eye
                            else R.drawable.ic_eye_close

                        Icon(
                            painter = painterResource(id = visibilityIcon),  // Cambio aquí: painter en lugar de imageVector
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña"
                            else
                                "Mostrar contraseña",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                passwordVisible = !passwordVisible
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        autoCorrect = false
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        // Borde en azul cuando está enfocado
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        // Borde en gris cuando no está enfocado
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 5.1.5) Botón / Carga (AnimatedVisibility)
                AnimatedVisibility(
                    visible = loginState !is AuthState.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ElevatedButton(
                        onClick = {
                            authViewModel.login(username.trim(), password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "Ingresar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                AnimatedVisibility(
                    visible = loginState is AuthState.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(56.dp)
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5.1.6) “¿Olvidaste tu contraseña?”
                TextButton(
                    onClick = { /* TODO: Acción “Olvidé mi contraseña” */ },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 5.2) Snackbar para errores
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
