package com.tuempresa.cursoscompose.sample

import com.tuempresa.cursoscompose.model.Course

object SampleData {
    val sampleCourses = listOf(
        Course(id = "c1", title = "Android Compose Básico", author = "Prof. A", progressPercent = 20),
        Course(id = "c2", title = "Kotlin Funcional", author = "Prof. B", progressPercent = 40),
        Course(id = "c3", title = "Bases de Datos con Firebase", author = "Prof. C", progressPercent = 0),
        Course(id = "c4", title = "Android XML Avanzado", author = "Prof. D", progressPercent = 10),
        Course(id = "c5", title = "Python", author = "Prof. E", progressPercent = 80),
        Course(id = "c6", title = "SQL SERVER 2014", author = "Prof. F", progressPercent = 30)
    )
}
