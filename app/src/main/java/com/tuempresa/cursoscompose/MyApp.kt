package com.tuempresa.cursoscompose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuempresa.cursoscompose.navigation.AppNavHost
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel

@Composable
fun MyApp() {
    // Tema base (puedes usar tu Theme si existe)
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            // Header debug: muestra UID y un botón para forzar recarga
            val auth = Firebase.auth
            val uid = auth.currentUser?.uid

            // Small layout: header + navhost
            Column(modifier = Modifier.fillMaxSize()) {
                DebugHeader(uid = uid)
                // Contenedor principal
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()) {

                    // NavHost principal (tu navegación existente)
                    AppNavHost(start = "catalog")
                }
            }
        }
    }
}

@Composable
private fun DebugHeader(uid: String?) {
    val vm: CoursesViewModel = viewModel()
    val courses by vm.courses.collectAsState()
    TopAppBar(
        title = {
            Column {
                Text("CursosCompose - Debug")
                Text("UID: ${uid ?: "no-auth"}", style = MaterialTheme.typography.caption)
            }
        },
        actions = {
            // Muestra conteo de cursos en el AppBar para diagnóstico rápido
            Box(modifier = Modifier.padding(end = 12.dp), contentAlignment = Alignment.Center) {
                Text("${courses.size} cursos", style = MaterialTheme.typography.body2)
            }
        }
    )
}
