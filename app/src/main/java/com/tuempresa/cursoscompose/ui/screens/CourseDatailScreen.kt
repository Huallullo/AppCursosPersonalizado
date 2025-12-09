package com.tuempresa.cursoscompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel

@Composable
fun CourseDetailScreen(course: Course, viewModel: CoursesViewModel, onBack: () -> Unit) {
    LaunchedEffect(course.id) {
        // opcional: track event
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row {
            TextButton(onClick = onBack) { Text("Atrás") }
            Spacer(modifier = Modifier.width(8.dp))
            Text(course.title, style = MaterialTheme.typography.h6)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Autor: ${course.author}")
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(progress = course.progressPercent.coerceIn(0,100)/100f, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.completeLesson(course.id) }, modifier = Modifier.fillMaxWidth()) {
            Text("Simular completar lección")
        }
    }
}
