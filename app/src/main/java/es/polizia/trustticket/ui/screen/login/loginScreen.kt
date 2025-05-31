package es.polizia.trustticket.ui.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definición de los colores principales:
private val DarkElectricBlue = Color(0xFF536878)
private val BrightYellow     = Color(0xFFFFEB3B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClicked: (email: String, password: String) -> Unit
) {
    // Estados para los campos de texto
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Fondo con degradado vertical de DarkElectricBlue a BrightYellow
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkElectricBlue, BrightYellow)
                )
            )
    ) {
        // Contenedor central para el formulario
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 16.dp)
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkElectricBlue
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Campo de Email (sin especificar “colors” explícitos)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = DarkElectricBlue) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Icono de usuario",
                            tint = DarkElectricBlue
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Contraseña (tampoco usar “colors” aquí)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = DarkElectricBlue) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Icono de contraseña",
                            tint = DarkElectricBlue
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        // Icono de ojo para mostrar/ocultar contraseña
                        val iconTint = DarkElectricBlue
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = iconTint,
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Iniciar Sesión
                Button(
                    onClick = { onLoginClicked(email.trim(), password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(FiftySixDp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkElectricBlue,
                        contentColor = BrightYellow
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "Ingresar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Texto adicional (“¿Olvidaste tu contraseña?”)
                TextButton(onClick = { /* TODO: Acción de “Olvidé mi contraseña” */ }) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = DarkElectricBlue,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private val FiftySixDp = 56.dp
