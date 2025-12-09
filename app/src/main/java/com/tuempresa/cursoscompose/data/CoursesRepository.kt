package com.tuempresa.cursoscompose.data

import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.UserProgress
import kotlinx.coroutines.flow.Flow


interface CoursesRepository {
    fun getCoursesStream(): Flow<List<Course>>
    suspend fun getCourseById(id: String): Course?
    fun getProgressStream(): Flow<List<UserProgress>>

    // Métodos para simular acciones (completar lección, descargar certificado)
    suspend fun simulateCompleteLesson(courseId: String)
    suspend fun addCourseToCatalog(course: Course)
    suspend fun deleteCourseInCatalog(courseId: String)

}
