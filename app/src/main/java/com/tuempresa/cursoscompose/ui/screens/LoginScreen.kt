package com.tuempresa.cursoscompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLogged: () -> Unit = {}) {
    // Si usas auth anon auto en MainActivity, este screen puede estar vacío o mostrar info.
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Autenticación anónima automática activada.", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Si quieres agregar login real, dime y lo implemento.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLogged) { Text("Continuar") }
    }
}
