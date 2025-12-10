package com.tuempresa.cursoscompose.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.UserProgress
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirestoreUserProgressRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = Firebase.auth
) : CoursesRepository {

    private val coursesCollection = firestore.collection("courses")
    private val TAG = "FirestoreRepo"

    private fun currentUid(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    }

    private fun tryEnsureAuthenticated(onComplete: (success: Boolean, exception: Exception?) -> Unit) {
        val current = auth.currentUser
        if (current != null) {
            onComplete(true, null)
            return
        }

        // No intentamos sign-in anónimo: la app requiere autenticación explícita (FB/GitHub).
        onComplete(false, IllegalStateException("User not authenticated - anonymous auth disabled"))
    }

    override fun getCoursesStream(): Flow<List<Course>> = callbackFlow {
        var registration: ListenerRegistration? = null

        val startListening = {
            registration = coursesCollection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.id
                            val title = doc.getString("title") ?: return@mapNotNull null
                            val author = doc.getString("author") ?: "Desconocido"
                            val progressPercent = doc.getLong("progressPercent")?.toInt() ?: 0
                            Course(
                                id = id,
                                title = title,
                                author = author,
                                progressPercent = progressPercent
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list).isSuccess
                }
            }
        }

        tryEnsureAuthenticated { success, exception ->
            if (success) {
                startListening()
            } else {
                val ex = exception ?: IllegalStateException("Authentication failed")
                close(ex)
            }
        }

        awaitClose { registration?.remove() }
    }

    override suspend fun getCourseById(id: String): Course? {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            throw IllegalStateException("Auth required to read course: user not authenticated")
        }

        val doc = coursesCollection.document(id).get().await()
        return if (doc.exists()) {
            val title = doc.getString("title") ?: return null
            val author = doc.getString("author") ?: "Desconocido"
            val progressPercent = doc.getLong("progressPercent")?.toInt() ?: 0
            Course(id = doc.id, title = title, author = author, progressPercent = progressPercent)
        } else null
    }

    override fun getProgressStream(): Flow<List<UserProgress>> = callbackFlow {
        var registration: ListenerRegistration? = null

        val startListeningForUid: (String) -> Unit = { uid ->
            val userProgressCollection =
                firestore.collection("userProgress").document(uid).collection("courses")
            registration = userProgressCollection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error); return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val courseId = doc.id
                            val completedLessons = doc.getLong("completedLessons")?.toInt() ?: 0
                            val totalLessons = doc.getLong("totalLessons")?.toInt() ?: 10
                            // safer cast: evita unchecked cast warning
                            val certificates = (doc.get("certificates") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                            UserProgress(
                                courseId = courseId,
                                completedLessons = completedLessons,
                                totalLessons = totalLessons,
                                certificates = certificates
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list).isSuccess
                }
            }
        }

        tryEnsureAuthenticated { success, exception ->
            if (success) {
                val uid = auth.currentUser?.uid
                if (uid != null) startListeningForUid(uid)
                else close(IllegalStateException("Authenticated but uid null"))
            } else {
                val ex = exception ?: IllegalStateException("Authentication failed")
                close(ex)
            }
        }

        awaitClose { registration?.remove() }
    }

    override suspend fun simulateCompleteLesson(courseId: String) {
        val uid = currentUid()
        val docRef = firestore.collection("userProgress").document(uid).collection("courses").document(courseId)

        firestore.runTransaction { tx ->
            val snapshot = tx.get(docRef)
            if (!snapshot.exists()) {
                tx.set(
                    docRef,
                    mapOf("completedLessons" to 1, "totalLessons" to 10)
                )
            } else {
                val current = snapshot.getLong("completedLessons")?.toInt() ?: 0
                val total = snapshot.getLong("totalLessons")?.toInt() ?: 10
                val newValue = (current + 1).coerceAtMost(total)
                tx.update(docRef, mapOf("completedLessons" to newValue))
            }
            null
        }.await()
    }

    // ---------- AÑADIDOS: operaciones sobre el catálogo (courses collection) ----------
    override suspend fun addCourseToCatalog(course: Course) {
        // Usa el id si ya existe, si no deja que Firestore genere el id
        val docRef = if (course.id.isNotBlank()) coursesCollection.document(course.id) else coursesCollection.document()
        val data = mapOf(
            "title" to course.title,
            "author" to course.author,
            "progressPercent" to course.progressPercent
        )
        docRef.set(data).await()
    }

    override suspend fun deleteCourseInCatalog(courseId: String) {
        coursesCollection.document(courseId).delete().await()
    }
}
