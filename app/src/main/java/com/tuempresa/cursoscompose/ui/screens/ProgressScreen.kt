package com.tuempresa.cursoscompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel
import com.tuempresa.cursoscompose.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProgressScreen(
    viewModel: CoursesViewModel,
    onBack: () -> Unit, // Se vuelve a añadir el onBack
    onMyCoursesClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onCertificatesClick: () -> Unit,
    onSignOut: () -> Unit
) {
    val authVm: AuthViewModel = viewModel()
    val user by authVm.user.collectAsState()

    val progress by viewModel.progress.collectAsState()

    val totalCompleted = progress.sumOf { it.completedLessons }
    val totalLessons = progress.sumOf { it.totalLessons }.coerceAtLeast(1)
    val overallProgress = (totalCompleted.toFloat() / totalLessons.toFloat())
    val overallProgressPercent = (overallProgress * 100).toInt()

    Scaffold(
        topBar = {
            // Se reintroduce el botón de navegación "Atrás"
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = { 
                    IconButton(onClick = onBack) { 
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar: photoUrl si existe, sino icono placeholder
            val avatarSize = 100.dp
            if (user?.photoUrl != null) {
                SubcomposeAsyncImage(
                    model = user?.photoUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(width = 2.dp, color = MaterialTheme.colors.primary, shape = CircleShape)
                ) {
                    when (painter.state) {
                        is coil.compose.AsyncImagePainter.State.Loading -> {
                            Box(modifier = Modifier
                                .size(avatarSize)
                                .clip(CircleShape)
                                .background(Color.LightGray), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colors.primary)
                            }
                        }
                        is coil.compose.AsyncImagePainter.State.Error -> {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Foto de Perfil",
                                modifier = Modifier
                                    .size(avatarSize)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                        }
                        else -> {
                            SubcomposeAsyncImageContent()
                        }
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(width = 2.dp, color = MaterialTheme.colors.primary, shape = CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Nombre desde Firebase
            Text(user?.displayName ?: "Usuario sin nombre", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
            Text(user?.email ?: "Sin correo", style = MaterialTheme.typography.body2, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Progreso general", style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                val animatedProgress by animateFloatAsState(targetValue = overallProgress)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = animatedProgress.coerceIn(0f,1f),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$overallProgressPercent%", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de cerrar sesión
            Button(onClick = {
                authVm.signOut()
                onSignOut()
            }, modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(44.dp)) {
                Text("Cerrar sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opciones de navegación funcionales
            ProfileOption(icon = Icons.AutoMirrored.Outlined.LibraryBooks, title = "Mis cursos", onClick = onMyCoursesClick)
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileOption(icon = Icons.Outlined.EmojiEvents, title = "Logros", onClick = onAchievementsClick)
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            ProfileOption(icon = Icons.Outlined.WorkspacePremium, title = "Certificados", onClick = onCertificatesClick)
        }
    }
}

@Composable
private fun ProfileOption(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colors.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos, contentDescription = "Ir a $title", modifier = Modifier.size(16.dp), tint = Color.Gray)
    }
}
