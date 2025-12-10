package com.tuempresa.cursoscompose.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.UserProgress
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreCoursesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CoursesRepository {

    private val auth = Firebase.auth

    private fun uidOrThrow(): String = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private fun userCoursesCollection(uid: String) = firestore.collection("userCourses").document(uid).collection("courses")

    override fun getCoursesStream(): Flow<List<Course>> = callbackFlow {
        val uid = try { uidOrThrow() } catch (e: Exception) { close(e); return@callbackFlow }

        val listener = userCoursesCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val courses = snapshot.documents.map { doc ->
                    Course(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        author = doc.getString("author") ?: "",
                        // Asegurar que el porcentaje esté entre 0 y 100
                        progressPercent = (doc.getLong("progressPercent")?.toInt() ?: 0).coerceIn(0, 100)
                    )
                }
                val result = trySend(courses)
                if (!result.isSuccess) {
                    Log.w("FirestoreCoursesRepo", "Failed to emit courses - channel closed or full")
                }
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getCourseById(id: String): Course? {
        return try {
            val doc = userCoursesCollection(uidOrThrow()).document(id).get().await()
            doc.toObject(Course::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            Log.e("FirestoreCoursesRepo", "Error getting course by ID", e)
            null
        }
    }

    override fun getProgressStream(): Flow<List<UserProgress>> = callbackFlow {
        val uid = try { uidOrThrow() } catch (e: Exception) { close(e); return@callbackFlow }
        val listener = firestore.collection("userProgress").document(uid).collection("progress")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val progressList = snapshot.toObjects(UserProgress::class.java)
                    val result = trySend(progressList)
                    if (!result.isSuccess) {
                        Log.w("FirestoreCoursesRepo", "Failed to emit progress - channel closed or full")
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addCourseToCatalog(course: Course) {
        try {
            val uid = uidOrThrow()
            val courseData = mapOf(
                "title" to course.title,
                "author" to course.author,
                "progressPercent" to 0,
            )
            userCoursesCollection(uid).add(courseData).await()
        } catch (e: Exception) {
            Log.e("FirestoreCoursesRepo", "Error adding course", e)
            throw e
        }
    }

    override suspend fun deleteCourseInCatalog(courseId: String) {
        try {
            userCoursesCollection(uidOrThrow()).document(courseId).delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreCoursesRepo", "Error deleting course", e)
            throw e
        }
    }

    override suspend fun simulateCompleteLesson(courseId: String) {
        try {
            val uid = uidOrThrow()
            val progressDocRef = firestore.collection("userProgress").document(uid).collection("progress").document(courseId)
            val courseDocRef = userCoursesCollection(uid).document(courseId)

            firestore.runTransaction { transaction ->
                val progressSnapshot = transaction.get(progressDocRef)
                val currentProgress = progressSnapshot.toObject(UserProgress::class.java)

                if (currentProgress == null) {
                    val newProgress = UserProgress(courseId = courseId, completedLessons = 1, totalLessons = 10)
                    transaction.set(progressDocRef, newProgress)
                    transaction.update(courseDocRef, "progressPercent", 10)
                } else {
                    val totalLessons = if (currentProgress.totalLessons > 0) currentProgress.totalLessons else 10
                    if (currentProgress.completedLessons < totalLessons) {
                        val newCompleted = currentProgress.completedLessons + 1
                        transaction.update(progressDocRef, "completedLessons", newCompleted)

                        val newPercent = ((newCompleted * 100) / totalLessons).coerceIn(0, 100)
                        transaction.update(courseDocRef, "progressPercent", newPercent)
                    }
                }
                null
            }.await()
        } catch (e: Exception) {
            Log.e("FirestoreCoursesRepo", "Error completing lesson", e)
            throw e
        }
    }
}
