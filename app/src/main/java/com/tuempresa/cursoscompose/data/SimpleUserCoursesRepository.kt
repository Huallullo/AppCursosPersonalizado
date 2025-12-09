package com.tuempresa.cursoscompose.data

import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tuempresa.cursoscompose.model.Course
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SimpleUserCoursesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val auth = Firebase.auth

    private fun uidOrThrow(): String = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private fun userCoursesCollection(uid: String) =
        firestore.collection("userCourses").document(uid).collection("courses")

    fun getCoursesStream(): Flow<List<Course>> = callbackFlow {
        val uid = try { uidOrThrow() } catch (e: Exception) { close(e); return@callbackFlow }
        val reg: ListenerRegistration = userCoursesCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            if (snapshot != null) {
                val list = snapshot.documents.map { d ->
                    Course(
                        id = d.id,
                        title = d.getString("title") ?: "",
                        author = d.getString("author") ?: "",
                        imageUrl = d.getString("imageUrl"),
                        progressPercent = d.getLong("progressPercent")?.toInt() ?: 0
                    )
                }
                trySend(list).isSuccess
            }
        }
        awaitClose { reg.remove() }
    }

    suspend fun addCourse(title: String, author: String) {
        val uid = uidOrThrow()
        val data = mapOf(
            "title" to title.trim(),
            "author" to author.trim(),
            "progressPercent" to 0
        )
        userCoursesCollection(uid).add(data).await()
    }

    suspend fun deleteCourse(courseId: String) {
        val uid = uidOrThrow()
        userCoursesCollection(uid).document(courseId).delete().await()
    }
}
