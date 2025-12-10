package com.tuempresa.cursoscompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.cursoscompose.data.LocalCoursesRepository
import com.tuempresa.cursoscompose.di.ViewModelFactory
import com.tuempresa.cursoscompose.ui.screens.MainScreen
import com.tuempresa.cursoscompose.ui.theme.CursosComposeTheme
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel

@Composable
fun MyApp() {
    // Configuración del ViewModel con el repositorio local
    val repository = remember { LocalCoursesRepository() }
    val factory = remember { ViewModelFactory(repository) }
    val vm: CoursesViewModel = viewModel(factory = factory)

    // Aplicar el tema personalizado de la aplicación
    CursosComposeTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            // La pantalla principal ahora contiene la barra de navegación
            MainScreen(vm = vm)
        }
    }
}
