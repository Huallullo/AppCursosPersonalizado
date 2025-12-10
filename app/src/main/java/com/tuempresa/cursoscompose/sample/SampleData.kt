package com.tuempresa.cursoscompose.sample

import com.tuempresa.cursoscompose.R
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.Lesson

object SampleData {

    val sampleCourses = listOf(
        Course(
            id = "c1",
            title = "Curso de Desarrollo de IA",
            author = "Abel Huallullo",
            progressPercent = 35,
            description = "Un curso completo para aprender los fundamentos y aplicaciones avanzadas de la Inteligencia Artificial en el desarrollo de software.",
            totalLessons = 25,
            difficulty = "Intermedio",
            lessons = listOf(
                Lesson("Lección 1: Introducción a la IA", 100),
                Lesson("Lección 2: Algoritmos de Búsqueda", 35),
                Lesson("Lección 3: Redes Neuronales", 0),
            ),
            coverRes = R.drawable.cover_c1
        ),
        Course(
            id = "c2",
            title = "Kotlin Funcional Avanzado",
            author = "JetBrains Team",
            progressPercent = 40,
            description = "Lleva tus habilidades de Kotlin al siguiente nivel con programación funcional, corrutinas y flujos.",
            lessons = listOf(
                Lesson("Lección 1: Conceptos básicos", 100),
                Lesson("Lección 2: Funciones de orden superior", 20),
            ),
            coverRes = R.drawable.cover_c2
        ),
        Course(
            id = "c3",
            title = "Firebase para Android",
            author = "Google Developers",
            progressPercent = 0,
            description = "Aprende a integrar Firebase en tus aplicaciones Android, desde la autenticación hasta bases de datos en tiempo real.",
            coverRes = R.drawable.cover_c3
        ),
        Course(
            id = "c4",
            title = "Testing en Jetpack Compose",
            author = "Android Devs",
            progressPercent = 10,
            description = "Asegura la calidad de tu app con tests unitarios y de UI para Jetpack Compose.",
            coverRes = R.drawable.cover_c4
        ),
        Course(
            id = "c5",
            title = "Python para Data Science",
            author = "Ana García",
            progressPercent = 80,
            coverRes = R.drawable.cover_c5
        ),
        Course(
            id = "c6",
            title = "Introducción a Docker",
            author = "Docker Inc.",
            progressPercent = 100,
            coverRes = R.drawable.cover_c6
        ),
        Course(
            id = "c7",
            title = "Programación Reactiva con RxJava",
            author = "Ana Gómez",
            progressPercent = 0,
            coverRes = R.drawable.cover_c7
        ),
        Course(
            id = "c8",
            title = "Machine Learning con Scikit-Learn",
            author = "Luis Ramos",
            progressPercent = 50,
            coverRes = R.drawable.cover_c8
        ),
        Course(
            id = "c9",
            title = "Desarrollo Web con Node.js",
            author = "Sofía Torres",
            progressPercent = 100,
            coverRes = R.drawable.cover_c9
        )
    )
}
