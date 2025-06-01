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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import es.polizia.trustticket.data.repository.EventRepository
import es.polizia.trustticket.data.repository.TicketRepository
import es.polizia.trustticket.ui.screen.event.EventDetailScreen
import es.polizia.trustticket.ui.screen.event.EventScreen
import es.polizia.trustticket.ui.screen.login.LoginScreen
import es.polizia.trustticket.ui.screen.tickets.MyTicketsScreen
import es.polizia.trustticket.ui.screen.tickets.TicketDetailScreen
import es.polizia.trustticket.ui.viewModel.EventsViewModel
import es.polizia.trustticket.ui.viewModel.AuthViewModel
import es.polizia.trustticket.ui.viewModel.MyTicketsViewModel
import es.polizia.trustticket.R

// Sealed class para las rutas de navegación con soporte para ImageVector y recursos drawable
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val iconRes: Int? = null
) {
    constructor(route: String, title: String, icon: ImageVector) : this(route, title, icon, null)
    constructor(route: String, title: String, iconRes: Int) : this(route, title, null, iconRes)

    object Login     : Screen("login",     "Login",         Icons.Default.Person)
    object Events    : Screen("events",    "Events",        R.drawable.ic_events)
    object MyTickets : Screen("mytickets", "Mis Tickets",   R.drawable.ic_ticket)
    object Logout    : Screen("logout",    "Logout",        R.drawable.ic_logout)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    // Observamos en qué destino estamos para saber si mostramos el bottom bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // ViewModels compartidos
    val eventsViewModel: EventsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EventsViewModel(EventRepository()) as T
            }
        }
    )

    val myTicketsViewModel: MyTicketsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MyTicketsViewModel(TicketRepository()) as T
            }
        }
    )

    val authViewModel: AuthViewModel = viewModel()

    // Solo mostramos el bottom bar en pantallas Events, MyTickets y Logout
    val bottomNavItems = listOf(
        Screen.Events,
        Screen.MyTickets,
        Screen.Logout
    )

    Scaffold(
        bottomBar = {
            val shouldShowBottomBar = currentDestination?.route in bottomNavItems.map { it.route }
            if (shouldShowBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                // Aquí manejamos tanto ImageVector como recursos drawable
                                when {
                                    screen.icon != null -> Icon(screen.icon, contentDescription = screen.title)
                                    screen.iconRes != null -> Icon(
                                        painter = painterResource(id = screen.iconRes),
                                        contentDescription = screen.title
                                    )
                                }
                            },
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
                        // ⭐ IMPORTANTE: Cargar eventos y tickets después del login exitoso
                        eventsViewModel.refreshEvents()
                        myTicketsViewModel.refresh()

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
                        onBack = {
                            // Actualizar tickets después de una posible compra
                            myTicketsViewModel.refresh()
                            navController.popBackStack()
                        }
                    )
                } else {
                    ErrorScreen(
                        message = "Event not found",
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // 4) PANTALLA DE "Mis Tickets"
            composable(Screen.MyTickets.route) {
                MyTicketsScreen(
                    viewModel = myTicketsViewModel,
                    onTicketClick = { ticket ->
                        navController.navigate("ticket_detail/${ticket.id}")
                    }
                )
            }

            // ⭐ 5) DETALLE DE TICKET - NUEVA RUTA AGREGADA
            composable(
                route = "ticket_detail/{ticketId}",
                arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
            ) { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getString("ticketId")
                val tickets by myTicketsViewModel.tickets.collectAsState()
                val ticket = tickets.find { it.id == ticketId }

                if (ticket != null) {
                    TicketDetailScreen(
                        ticket = ticket,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    ErrorScreen(
                        message = "Ticket not found",
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // 6) PANTALLA DE LOGOUT
            composable(Screen.Logout.route) {
                // En cuanto el usuario selecciona la pestaña "Logout", ejecutamos el logout y volvemos al Login
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
                // (Mientras se ejecuta, podemos mostrar un texto "Cerrando sesión…" o simplemente nada):
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