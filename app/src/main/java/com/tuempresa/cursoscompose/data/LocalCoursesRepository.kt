package com.tuempresa.cursoscompose.data

import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.UserProgress
import com.tuempresa.cursoscompose.sample.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalCoursesRepository : CoursesRepository {

    private val _courses = MutableStateFlow(SampleData.sampleCourses)

    // Se añade más contenido para que la app se vea más completa
    private val _progress = MutableStateFlow(listOf(
        UserProgress(courseId = "c1", completedLessons = 2, totalLessons = 10),
        UserProgress(courseId = "c2", completedLessons = 4, totalLessons = 10),
        UserProgress(courseId = "c4", completedLessons = 1, totalLessons = 10),
        UserProgress(courseId = "c5", completedLessons = 8, totalLessons = 10),
        UserProgress(courseId = "c6", completedLessons = 10, totalLessons = 10, certificates = listOf("Certificado de Introducción a Docker")),
        // Progreso para los nuevos cursos añadidos
        UserProgress(courseId = "c8", completedLessons = 5, totalLessons = 10),
        UserProgress(courseId = "c9", completedLessons = 10, totalLessons = 10, certificates = listOf("Certificado de Desarrollo Web con Node.js"))
    ))

    override fun getCoursesStream(): Flow<List<Course>> = _courses.asStateFlow()
    override fun getProgressStream(): Flow<List<UserProgress>> = _progress.asStateFlow()

    override suspend fun getCourseById(id: String): Course? {
        return _courses.value.find { it.id == id }
    }

    override suspend fun simulateCompleteLesson(courseId: String) {
        val courses = _courses.value.toMutableList()
        val courseIdx = courses.indexOfFirst { it.id == courseId }
        if (courseIdx >= 0) {
            val course = courses[courseIdx]
            val newPercent = (course.progressPercent + 10).coerceAtMost(100)
            courses[courseIdx] = course.copy(progressPercent = newPercent)
            _courses.emit(courses)

            val progressList = _progress.value.toMutableList()
            val progressIdx = progressList.indexOfFirst { it.courseId == courseId }

            if (progressIdx >= 0) {
                val progress = progressList[progressIdx]
                if (progress.completedLessons < progress.totalLessons) {
                    val newCompleted = progress.completedLessons + 1
                    val newCertificates = if (newCompleted == progress.totalLessons) {
                        progress.certificates + "Certificado de '${course.title}'"
                    } else {
                        progress.certificates
                    }
                    progressList[progressIdx] = progress.copy(
                        completedLessons = newCompleted,
                        certificates = newCertificates
                    )
                }
            } else {
                progressList.add(UserProgress(courseId = courseId, completedLessons = 1, totalLessons = 10))
            }
            _progress.emit(progressList)
        }
    }

    override suspend fun addCourseToCatalog(course: Course) {
        val list = _courses.value.toMutableList()
        val safeId = if (course.id.isBlank()) "local_${System.currentTimeMillis()}" else course.id
        val courseToAdd = if (course.id.isBlank()) course.copy(id = safeId) else course
        if (!list.any { it.id == courseToAdd.id }) {
            list.add(courseToAdd)
        }
        _courses.emit(list)
    }

    override suspend fun deleteCourseInCatalog(courseId: String) {
        _courses.emit(_courses.value.filterNot { it.id == courseId })
        _progress.emit(_progress.value.filterNot { it.courseId == courseId })
    }
}
