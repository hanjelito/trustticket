// src/main/java/es/polizia/trustticket/ui/navigation/AppNavigator.kt
package es.polizia.trustticket.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import es.polizia.trustticket.ui.screen.event.EventDetailScreen
import es.polizia.trustticket.ui.screen.event.EventScreen
import es.polizia.trustticket.ui.screen.login.LoginScreen
import es.polizia.trustticket.ui.viewModel.EventsViewModel
import es.polizia.trustticket.ui.viewModel.AuthViewModel

// Sealed class para las rutas de navegación
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login   : Screen("login",   "Login",   Icons.Default.Person)
    object Events  : Screen("events",  "Events",  Icons.Default.Face)
    object MyEvent : Screen("MyEvent", "Mis Eventos", Icons.Default.Favorite)
    object Logout  : Screen("logout",  "Logout",  Icons.Default.Close)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    // Observamos en qué destino estamos para saber si mostramos el bottom bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // ViewModels compartidos
    val eventsViewModel: EventsViewModel = viewModel()
    val authViewModel: AuthViewModel     = viewModel()

    // Solo mostramos el bottom bar en pantallas Events, MyEvent y Logout
    val bottomNavItems = listOf(
        Screen.Events,
        Screen.MyEvent,
        Screen.Logout
    )

    Scaffold(
        bottomBar = {
            val shouldShowBottomBar = currentDestination?.route in bottomNavItems.map { it.route }
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1) PANTALLA DE LOGIN
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { auth_jwt ->
                        // Almacenamos auth_jwt (ya lo guarda el ViewModel en SessionManager)
                        // Luego navegamos a Events y borramos Login de la pila
                        navController.navigate(Screen.Events.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    authViewModel = authViewModel
                )
            }

            // 2) PANTALLA DE EVENTOS
            composable(Screen.Events.route) {
                EventScreen(
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    },
                    viewModel = eventsViewModel
                )
            }

            // 3) DETALLE DE CADA EVENTO
            composable(
                route = "event_detail/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId")
                val events by eventsViewModel.events.collectAsState()
                val event = events.find { it.id == eventId }
                if (event != null) {
                    EventDetailScreen(
                        event = event,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    ErrorScreen(
                        message = "Event not found",
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // 4) PANTALLA DE “Mis Eventos” (Profile)
            composable(Screen.MyEvent.route) {
                ProfileScreen()
            }

            // 5) PANTALLA DE LOGOUT
            composable(Screen.Logout.route) {
                // En cuanto el usuario selecciona la pestaña “Logout”, ejecutamos el logout y volvemos al Login
                LaunchedEffect(Unit) {
                    // 1) Borramos token del ViewModel / SessionManager:
                    authViewModel.logout()

                    // 2) Navegamos a Login y limpiamos la pila
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
                // (Mientras se ejecuta, podemos mostrar un texto “Cerrando sesión…” o simplemente nada):
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cerrando sesión...",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(
    message: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Error") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "❌", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack) {
                Text("Go Back")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profile") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "👤", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Profile Screen", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coming soon...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "⚙️", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Settings Screen", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coming soon...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
