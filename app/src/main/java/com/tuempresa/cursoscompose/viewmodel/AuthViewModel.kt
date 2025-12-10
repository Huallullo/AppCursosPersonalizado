package com.tuempresa.cursoscompose.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tuempresa.cursoscompose.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        viewModelScope.launch {
            _user.value = firebaseAuth.currentUser
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        // Forzar signOut si el usuario es anónimo para requerir un login explícito
        val current = auth.currentUser
        if (current?.isAnonymous == true) {
            AuthManager.signOut()
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    fun signOut() {
        AuthManager.signOut()
    }
}
