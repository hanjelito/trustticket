package es.polizia.trustticket.ui.navigation

import androidx.compose.foundation.layout.*
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
    // Observamos el back stack para saber en qué destino estamos
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // ViewModel compartido para eventos
    val eventsViewModel: EventsViewModel = viewModel()

    // Definimos las rutas que queremos que aparezcan en el bottom bar
    // (usualmente excluimos la pantalla de login para que no muestre el bottom bar ahí)
    val bottomNavItems = listOf(
        Screen.Events,
        Screen.MyEvent,
        Screen.Logout
    )

    Scaffold(
        bottomBar = {
            // Solo mostramos el BottomBar si la ruta actual está en bottomNavItems
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
                                    // Evitar apilar varias veces la misma pantalla
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
            // Cambiamos el startDestination a la ruta de Login
            startDestination = Screen.Login.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1) Registro de la pantalla de LOGIN
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginClicked = { email, password ->
                        // Aquí va tu lógica de validación de credenciales.
                        // Por ejemplo:
                        if (email == "usuario@example.com" && password == "1234") {
                            // Si el login es correcto, navegamos a “events”
                            navController.navigate(Screen.Events.route) {
                                // Limpiamos la pila de navegación para que el usuario
                                // no regrese al login con “back”
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            // Puedes mostrar un mensaje de error, Snackbar, etc.
                            // Por simplicidad, aquí no hacemos nada.
                        }
                    }
                )
            }

            // 2) Pantalla de eventos
            composable(Screen.Events.route) {
                EventScreen(
                    onEventClick = { eventId ->
                        navController.navigate("event_detail/$eventId")
                    },
                    viewModel = eventsViewModel
                )
            }

            // 3) Detalle de evento (recibe eventId)
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

            // 4) Pantalla de “Mis Eventos” (Profile/MyEvent)
            composable(Screen.MyEvent.route) {
                ProfileScreen()
            }

            // 5) Pantalla de Configuración/Logout
            composable(Screen.Logout.route) {
                SettingsScreen()
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
            Text(
                text = "❌",
                style = MaterialTheme.typography.displayLarge
            )
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
