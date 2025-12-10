package com.tuempresa.cursoscompose.auth

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun startFacebookSignIn(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
    }

    suspend fun signInWithFacebookToken(token: AccessToken): Result<Boolean> {
        return try {
            val credential = FacebookAuthProvider.getCredential(token.token)
            auth.signInWithCredential(credential).await()
            updateProfileWithFacebookData(token)
            Result.success(true)
        } catch (e: Exception) {
            Log.e("AuthManager", "Error in signInWithFacebookToken", e)
            Result.failure(e)
        }
    }

    private suspend fun updateProfileWithFacebookData(accessToken: AccessToken) {
        val photoUrl = suspendCoroutine<String?> { continuation ->
            val request = GraphRequest.newMeRequest(accessToken) { obj, _ ->
                val url = obj?.optJSONObject("picture")?.optJSONObject("data")?.optString("url")
                continuation.resume(url)
            }
            val parameters = Bundle()
            parameters.putString("fields", "id,name,picture.type(large)")
            request.parameters = parameters
            request.executeAsync()
        }

        Log.d("AuthManager", "Facebook photo URL: $photoUrl")
        if (photoUrl.isNullOrEmpty()) return

        try {
            val profileUpdates = userProfileChangeRequest {
                photoUri = Uri.parse(photoUrl)
            }
            auth.currentUser?.updateProfile(profileUpdates)?.await()
            Log.d("AuthManager", "Firebase profile updated successfully.")
            auth.currentUser?.reload()?.await()
            Log.d("AuthManager", "Firebase user reloaded successfully.")
        } catch (e: Exception) {
            Log.w("AuthManager", "Failed to update profile or reload user.", e)
        }
    }

    fun startGitHubSignIn(activity: Activity, onFailure: (Exception) -> Unit, onSuccess: () -> Unit) {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("allow_signup", "false")
        auth.startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener { authResult ->
                handleGitHubSignInSuccess(authResult, onSuccess)
            }
            .addOnFailureListener { onFailure(it) }
    }

    private fun handleGitHubSignInSuccess(authResult: AuthResult, onCompletion: () -> Unit) {
        val photoUrl = authResult.additionalUserInfo?.profile?.get("avatar_url") as? String
        Log.d("AuthManager", "GitHub photo URL: $photoUrl")

        if (photoUrl.isNullOrEmpty()) {
            onCompletion()
            return
        }

        val profileUpdates = userProfileChangeRequest {
            photoUri = Uri.parse(photoUrl)
        }

        auth.currentUser?.updateProfile(profileUpdates)?.continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception!!
            Log.d("AuthManager", "GitHub profile updated successfully.")
            auth.currentUser!!.reload()
        }?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                Log.d("AuthManager", "GitHub user reloaded successfully.")
            } else {
                Log.w("AuthManager", "Failed to reload user after GitHub sign-in.", reloadTask.exception)
            }
            onCompletion()
        }
    }

    fun signOut() {
        auth.signOut()
        LoginManager.getInstance().logOut()
    }
}
