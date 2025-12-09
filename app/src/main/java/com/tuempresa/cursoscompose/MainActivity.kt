package com.tuempresa.cursoscompose

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carga UI inmediatamente (evita pantalla en blanco)
        setContent { MyApp() }

        // Intento de signin anónimo (no bloquea UI)
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    Log.d(TAG, "signInAnonymously: OK uid=$uid")
                    runOnUiThread {
                        Toast.makeText(this, "Signed in anonymously", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val msg = task.exception?.message ?: "Unknown auth error"
                    Log.e(TAG, "signInAnonymously: FAILED -> $msg", task.exception)
                    runOnUiThread {
                        Toast.makeText(this, "AUTH FAILED: $msg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Log.d(TAG, "Already signed in: uid=${auth.currentUser?.uid}")
        }
    }
}
