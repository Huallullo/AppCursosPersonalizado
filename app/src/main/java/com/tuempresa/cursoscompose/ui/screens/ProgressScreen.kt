package com.tuempresa.cursoscompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel

@Composable
fun ProgressScreen(viewModel: CoursesViewModel, onBack: () -> Unit) {
    val progress by viewModel.progress.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            TextButton(onClick = onBack) { Text("Atrás") }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mi progreso", style = MaterialTheme.typography.h6)
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (progress.isEmpty()) Text("Sin progreso aún")
        else {
            LazyColumn {
                items(progress) { p ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Curso: ${p.courseId}")
                            Text("${p.completedLessons}/${p.totalLessons} lecciones")
                            LinearProgressIndicator(progress = p.completedLessons / p.totalLessons.toFloat(), modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}
