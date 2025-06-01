// ui/screen/register/RegisterScreen.kt
package es.polizia.trustticket.ui.screen.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.polizia.trustticket.R
import es.polizia.trustticket.ui.viewModel.AuthViewModel
import es.polizia.trustticket.ui.viewModel.RegisterState
import es.polizia.trustticket.ui.viewModel.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados de validación
    var showErrors by remember { mutableStateOf(false) }

    // Observar estado del registro
    val registerState by authViewModel.registerState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Efecto para manejar resultados del registro
    LaunchedEffect(registerState) {
        println("🚀 LaunchedEffect ejecutado con estado: $registerState")
        val currentState = registerState
        when (currentState) {
            is RegisterState.Success -> {
                println("✅ Registro exitoso, mostrando snackbar")
                snackbarHostState.showSnackbar("¡Registro exitoso! Iniciando sesión...")
                println("⏳ Esperando 1 segundo...")
                kotlinx.coroutines.delay(500)
                println("🔄 Auto-login: usando las credenciales para hacer login automático")
                // Auto-login con las credenciales que acabamos de registrar
                authViewModel.login(username, password)
            }
            is RegisterState.Error -> {
                println("❌ Error en registro: ${currentState.message}")
                snackbarHostState.showSnackbar(currentState.message)
            }
            else -> {
                println("ℹ️ Estado: $currentState")
            }
        }
    }

    // Efecto para manejar el auto-login después del registro
    val loginState by authViewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        when (loginState) {
            is AuthState.Success -> {
                println("🎉 Auto-login exitoso después del registro")
                snackbarHostState.showSnackbar("¡Bienvenido a TrustTicket!")
                kotlinx.coroutines.delay(500)
                onRegisterSuccess() // Esto ahora navega a la pantalla principal (Events)
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar("Registro exitoso, pero hubo un error al iniciar sesión. Inicia sesión manualmente.")
                kotlinx.coroutines.delay(500)
                onRegisterSuccess() // Navega al login para que haga login manual
            }
            else -> { /* No hacer nada */ }
        }
    }

    // Función de validación
    fun validateForm(): Boolean {
        return name.isNotBlank() &&
                surname.isNotBlank() &&
                username.isNotBlank() &&
                phone.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword == password
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // TopAppBar personalizada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToLogin) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tarjeta del formulario
            ElevatedCard(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Únete a TrustTicket",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Campos del formulario
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        isError = showErrors && name.isBlank(),
                        supportingText = if (showErrors && name.isBlank()) {
                            { Text("El nombre es requerido") }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = surname,
                        onValueChange = { surname = it },
                        label = { Text("Apellido") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        isError = showErrors && surname.isBlank(),
                        supportingText = if (showErrors && surname.isBlank()) {
                            { Text("El apellido es requerido") }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de usuario") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        isError = showErrors && username.isBlank(),
                        supportingText = if (showErrors && username.isBlank()) {
                            { Text("El nombre de usuario es requerido") }
                        } else {
                            // Mostrar errores específicos del servidor si existen
                            val fieldError = (registerState as? RegisterState.Error)?.fieldErrors?.get("username")?.firstOrNull()
                            if (fieldError != null) { { Text(fieldError) } } else null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        isError = showErrors && phone.isBlank(),
                        supportingText = if (showErrors && phone.isBlank()) {
                            { Text("El teléfono es requerido") }
                        } else {
                            val fieldError = (registerState as? RegisterState.Error)?.fieldErrors?.get("phone")?.firstOrNull()
                            if (fieldError != null) { { Text(fieldError) } } else null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        isError = showErrors && email.isBlank(),
                        supportingText = if (showErrors && email.isBlank()) {
                            { Text("El email es requerido") }
                        } else {
                            val fieldError = (registerState as? RegisterState.Error)?.fieldErrors?.get("email")?.firstOrNull()
                            if (fieldError != null) { { Text(fieldError) } } else null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) R.drawable.ic_eye else R.drawable.ic_eye_close
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        isError = showErrors && password.isBlank(),
                        supportingText = if (showErrors && password.isBlank()) {
                            { Text("La contraseña es requerida") }
                        } else {
                            val fieldError = (registerState as? RegisterState.Error)?.fieldErrors?.get("password")?.firstOrNull()
                            if (fieldError != null) { { Text(fieldError) } } else null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (confirmPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_close
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible }
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        isError = showErrors && (confirmPassword != password || confirmPassword.isBlank()),
                        supportingText = if (showErrors && confirmPassword.isBlank()) {
                            { Text("Confirma tu contraseña") }
                        } else if (showErrors && confirmPassword != password) {
                            { Text("Las contraseñas no coinciden") }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de registro o loading
                    AnimatedVisibility(
                        visible = registerState !is RegisterState.Loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ElevatedButton(
                            onClick = {
                                showErrors = true
                                if (validateForm()) {
                                    authViewModel.register(
                                        name = name,
                                        surname = surname,
                                        username = username,
                                        phone = phone,
                                        email = email,
                                        password = password
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Crear cuenta",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = registerState is RegisterState.Loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(56.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Link para ir al login
                    TextButton(
                        onClick = onNavigateToLogin
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta? Inicia sesión",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}