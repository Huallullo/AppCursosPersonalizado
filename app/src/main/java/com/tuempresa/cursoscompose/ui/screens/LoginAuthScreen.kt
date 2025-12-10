package com.tuempresa.cursoscompose.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tuempresa.cursoscompose.R
import com.tuempresa.cursoscompose.auth.AuthManager
import com.tuempresa.cursoscompose.ui.screens.BottomBarScreen
import com.tuempresa.cursoscompose.viewmodel.AuthViewModel
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch

@Composable
fun LoginAuthScreen(navController: NavController) {
    val authVm: AuthViewModel = viewModel()
    val user by authVm.user.collectAsState()
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // Estados UI
    var loadingFB by remember { mutableStateOf(false) }
    var loadingGH by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Navegar a Home cuando user no sea nulo. Si se desloguea, el NavHost lo traerá de vuelta.
    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate(BottomBarScreen.Home.route) {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // El resto de la UI solo se muestra si el usuario es nulo
    if (user == null) {
        Scaffold(scaffoldState = scaffoldState) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFeef2ff), Color.White)))) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo grande centrado con fondo circular colorido
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                        // fondo circular con degradado
                        Box(modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(70.dp))
                            .background(Brush.radialGradient(listOf(Color(0xFF7C4DFF), Color(0xFF5E2AD6))))
                        )
                        SubcomposeAsyncImage(
                            model = R.drawable.ic_launcher_foreground,
                            contentDescription = "Logo",
                            modifier = Modifier.size(88.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("CursosCompose", style = MaterialTheme.typography.h5, color = Color(0xFF2A2A2A))
                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFE53935))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMessage ?: "", color = Color(0xFFE53935))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón Facebook
                    Button(
                        onClick = {
                            loadingFB = true
                            val activity = (context as Activity)
                            AuthManager.startFacebookSignIn(activity)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1877F2)),
                        enabled = !loadingFB && !loadingGH
                    ) {
                        if (loadingFB) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp)) else Text("Iniciar sesión con Facebook", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón GitHub
                    Button(
                        onClick = {
                            loadingGH = true
                            val activity = (context as Activity)
                            AuthManager.startGitHubSignIn(activity, onFailure = { exc ->
                                loadingGH = false
                                errorMessage = "GitHub login failed: ${exc.message}"
                                coroutineScope.launch { scaffoldState.snackbarHostState.showSnackbar(errorMessage ?: "Error GH") }
                            }, onSuccess = {
                                loadingGH = false
                            })
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        enabled = !loadingFB && !loadingGH
                    ) {
                        if (loadingGH) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp)) else Text("Iniciar sesión con GitHub", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("O accede con tu cuenta para continuar", color = Color.Gray)
                }
            }
        }
    }
}
