package com.tuempresa.cursoscompose.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.cursoscompose.data.CoursesRepository
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.UserProgress
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val repository: CoursesRepository
) : ViewModel() {

    // Exponer lista de cursos como StateFlow para la UI
    val courses: StateFlow<List<Course>> =
        repository.getCoursesStream()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Exponer el progreso del usuario
    val progress: StateFlow<List<UserProgress>> =
        repository.getProgressStream()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    // Ejemplo: wrapper para añadir curso (UI -> ViewModel -> Repo)
    fun addCourseToCatalog(course: Course) {
        viewModelScope.launch {
            try {
                repository.addCourseToCatalog(course)
            } catch (e: Exception) {
                Log.e("CoursesViewModel", "addCourseToCatalog failed", e)
            }
        }
    }

    // Wrapper para borrar curso
    fun deleteCourseInCatalog(courseId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCourseInCatalog(courseId)
            } catch (e: Exception) {
                Log.e("CoursesViewModel", "deleteCourseInCatalog failed", e)
            }
        }
    }

    // Wrapper para completar lección
    fun completeLesson(courseId: String) {
        viewModelScope.launch {
            try {
                repository.simulateCompleteLesson(courseId)
            } catch (e: Exception) {
                Log.e("CoursesViewModel", "completeLesson failed", e)
            }
        }
    }

    // Si necesitas recuperar curso por id sin suspender en la UI:
    suspend fun getCourseById(id: String): Course? = repository.getCourseById(id)
}
