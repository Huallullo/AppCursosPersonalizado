package com.tuempresa.cursoscompose.model

// Modelo de curso (mantener exactamente el constructor que usas)
data class Course(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val imageUrl: String? = null,
    val progressPercent: Int = 0
)

data class UserProgress(
    val courseId: String = "",
    val completedLessons: Int = 0,
    val totalLessons: Int = 10,
    val certificates: List<String> = emptyList()
)
