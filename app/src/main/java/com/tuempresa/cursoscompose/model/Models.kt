package com.tuempresa.cursoscompose.model

// Modelo de Lección para el detalle del curso
data class Lesson(
    val title: String,
    val progress: Int // Progreso de 0 a 100
)

// Modelo de Curso actualizado con coverRes opcional (resource drawable)
data class Course(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val progressPercent: Int = 0,
    val description: String = "Descripción detallada del curso...",
    val totalLessons: Int = 25,
    val difficulty: String = "Intermedio",
    val lessons: List<Lesson> = emptyList(),
    val coverRes: Int? = null // Recurso drawable para la portada (opcional)
)

// Modelo para el progreso general del usuario
data class UserProgress(
    val courseId: String = "",
    val completedLessons: Int = 0,
    val totalLessons: Int = 10, // Usado para el cálculo de progreso, puede coexistir
    val certificates: List<String> = emptyList()
)
