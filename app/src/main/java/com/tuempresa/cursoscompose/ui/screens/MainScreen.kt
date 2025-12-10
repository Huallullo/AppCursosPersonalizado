package com.tuempresa.cursoscompose.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.tuempresa.cursoscompose.navigation.AppNavHost
import com.tuempresa.cursoscompose.ui.theme.Primary
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel

sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarScreen("home", "Inicio", Icons.Outlined.Code)
    object Catalog : BottomBarScreen("catalog", "Catálogo", Icons.Outlined.Code)
    object Profile : BottomBarScreen("profile", "Perfil", Icons.Outlined.AccountCircle)
}

@Composable
fun MainScreen(vm: CoursesViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            // Sólo mostrar la barra inferior en rutas específicas (no en login)
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = listOf(
                BottomBarScreen.Home.route,
                BottomBarScreen.Catalog.route,
                BottomBarScreen.Profile.route
            ).any { route -> currentDestination?.hierarchy?.any { it.route == route } == true }

            if (showBottomBar) {
                BottomNavigation {
                    val bottomBarDestinations = listOf(
                        BottomBarScreen.Home,
                        BottomBarScreen.Catalog,
                        BottomBarScreen.Profile,
                    )
                    bottomBarDestinations.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        val scale by animateFloatAsState(if (selected) 1.12f else 1f)
                        BottomNavigationItem(
                            icon = { Icon(imageVector = screen.icon, contentDescription = screen.title, tint = if (selected) Primary else Color.Gray, modifier = Modifier.size((24 * scale).dp)) },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            alwaysShowLabel = false,
                            selectedContentColor = Primary,
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavHost(
            vm = vm,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
