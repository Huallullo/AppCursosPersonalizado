package com.tuempresa.cursoscompose.data

import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

/**
 * Implementación local in-memory para desarrollo / pruebas.
 *
 * Ajusta si usas Room o almacenamiento persistente.
 */
class LocalCoursesRepository : CoursesRepository {

    // Estado interno in-memory
    private val _courses = MutableStateFlow<List<Course>>(
        listOf(
            // Ejemplo de contenido inicial (opcional). Comenta o borra si no lo quieres.
            Course(id = "c1", title = "Kotlin Compose Basics", author = "Abel Huallullo", imageUrl = "", progressPercent = 0),
            Course(id = "c2", title = "Advanced Android", author = "Team", imageUrl = "", progressPercent = 0)
        )
    )

    // Exponer solo Flow público
    override fun getCoursesStream(): Flow<List<Course>> = _courses.asStateFlow()

    override suspend fun getCourseById(id: String): Course? {
        return _courses.value.find { it.id == id }
    }

    // Stub para progreso de usuario
    override fun getProgressStream(): Flow<List<UserProgress>> {
        // Si no usas UserProgress localmente, devuelve lista vacía
        val empty: List<UserProgress> = emptyList()
        return MutableStateFlow(empty)
    }

    override suspend fun simulateCompleteLesson(courseId: String) {
        val list = _courses.value.toMutableList()
        val idx = list.indexOfFirst { it.id == courseId }
        if (idx >= 0) {
            val c = list[idx]
            val newPercent = (c.progressPercent + 10).coerceAtMost(100)
            list[idx] = c.copy(progressPercent = newPercent)
            _courses.emit(list)
        }
    }

    // ----------------- Métodos añadidos requeridos -----------------
    override suspend fun addCourseToCatalog(course: Course) {
        val list = _courses.value.toMutableList()
        // Si el id está en blanco, generar uno simple (puedes cambiar por UUID)
        val safeId = if (course.id.isBlank()) "local_${System.currentTimeMillis()}" else course.id
        val courseToAdd = if (course.id.isBlank()) course.copy(id = safeId) else course

        val idx = list.indexOfFirst { it.id == courseToAdd.id }
        if (idx >= 0) {
            list[idx] = courseToAdd
        } else {
            list.add(courseToAdd)
        }
        _courses.emit(list)
    }

    override suspend fun deleteCourseInCatalog(courseId: String) {
        val newList = _courses.value.filterNot { it.id == courseId }
        _courses.emit(newList)
    }
}
