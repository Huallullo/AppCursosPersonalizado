package com.tuempresa.cursoscompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.tuempresa.cursoscompose.auth.AuthManager
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    // Registrar un launcher para Activity Result API y reenviar a CallbackManager
    private val fbActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        // reenviar al callback manager; Facebook usa requestCode=64206 internamente
        callbackManager.onActivityResult(64206, result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Carga UI inmediatamente (evita pantalla en blanco)
        setContent { MyApp() }

        // Registramos callback global para login de Facebook
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "Facebook login success")
                // Intercambiar token de Facebook por credencial Firebase
                val accessToken = result.accessToken
                lifecycleScope.launch {
                    try {
                        val res = AuthManager.signInWithFacebookToken(accessToken)
                        if (res.isSuccess) {
                            val uid = Firebase.auth.currentUser?.uid
                            Log.d(TAG, "Firebase signInWithFacebook: OK uid=$uid")
                            runOnUiThread { Toast.makeText(this@MainActivity, "Login exitoso: uid=$uid", Toast.LENGTH_SHORT).show() }
                        } else {
                            val ex = res.exceptionOrNull()
                            Log.e(TAG, "Firebase signInWithFacebook failed", ex)
                            runOnUiThread { Toast.makeText(this@MainActivity, "Autenticación FB fallida: ${ex?.message}", Toast.LENGTH_LONG).show() }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al autenticar con Firebase usando token FB", e)
                        runOnUiThread { Toast.makeText(this@MainActivity, "Error auth: ${e.message}", Toast.LENGTH_LONG).show() }
                    }
                }
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login canceled")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Facebook login error", error)
                runOnUiThread { Toast.makeText(this@MainActivity, "FB login error: ${error.message}", Toast.LENGTH_LONG).show() }
            }
        })

        // Nota: ya no se realiza signInAnonymously. La app requiere login explícito (Facebook/GitHub).
        val auth = Firebase.auth
        Log.d(TAG, "Current user uid=${auth.currentUser?.uid}")
    }

    // onActivityResult como fallback para compatibilidad con versiones antiguas
    @Suppress("Deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
