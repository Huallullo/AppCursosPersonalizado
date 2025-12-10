package com.tuempresa.cursoscompose.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.cursoscompose.auth.AuthManager
import com.tuempresa.cursoscompose.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import androidx.compose.foundation.border
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.ui.theme.Primary
import com.tuempresa.cursoscompose.ui.theme.PrimaryVariant
import com.tuempresa.cursoscompose.ui.theme.Secondary
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    popularCourses: List<Course>,
    certificatesCount: Int,
    onCourseClick: (String) -> Unit,
    onCatalogClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCertificatesClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scaffoldState = rememberScaffoldState()
    // scope para lanzar coroutines desde callbacks no-suspend
    val uiScope = rememberCoroutineScope()

    // Obtener usuario autenticado para saludo
    val authVm: AuthViewModel = viewModel()
    val user by authVm.user.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hola, ${user?.displayName ?: ""}", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "", color = Color.Gray, fontSize = 12.sp)
                    }
                },
                actions = {
                    // Avatar (usar photoUrl si existe)
                    IconButton(onClick = onProfileClick) {
                        val avatarSize = 40.dp
                        if (user?.photoUrl != null) {
                            SubcomposeAsyncImage(
                                model = user?.photoUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(avatarSize).clip(CircleShape).border(width = 2.dp, color = MaterialTheme.colors.primary, shape = CircleShape)
                            ) {
                                when (painter.state) {
                                    is coil.compose.AsyncImagePainter.State.Loading -> {
                                        Box(modifier = Modifier.size(avatarSize).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(modifier = Modifier.size(12.dp), color = MaterialTheme.colors.primary)
                                        }
                                    }
                                    is coil.compose.AsyncImagePainter.State.Error -> {
                                        Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "Avatar", tint = Primary, modifier = Modifier.size(36.dp))
                                    }
                                    else -> SubcomposeAsyncImageContent()
                                }
                            }
                        } else {
                            Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "Avatar", tint = Primary, modifier = Modifier.size(36.dp))
                        }
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("¿Qué te gustaría aprender hoy?", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CategoryCard("Programación", Icons.Outlined.Code, Brush.linearGradient(listOf(Primary.copy(alpha = 0.15f), Color.White)), Modifier.weight(1f))
                    CategoryCard("Diseño", Icons.Outlined.DesignServices, Brush.linearGradient(listOf(Secondary.copy(alpha = 0.12f), Color.White)), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cursos populares", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onCatalogClick) { Text("Ver todos") }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(popularCourses) { course ->
                        PopularCourseCard(course = course, onClick = { onCourseClick(course.id) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Tarjeta certificados con badge
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCertificatesClick)
                        .animateContentSize(),
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = Color(0xFFFFF9E6),
                    elevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Icon(imageVector = Icons.Outlined.EmojiEvents, contentDescription = "Certificados", tint = Primary, modifier = Modifier.size(36.dp))
                            if (certificatesCount > 0) {
                                // badge
                                Box(modifier = Modifier
                                    .offset(x = 20.dp, y = (-6).dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935)), contentAlignment = Alignment.Center) {
                                    Text("${certificatesCount}", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Mis Certificados", fontWeight = FontWeight.Bold)
                            Text("Has obtenido $certificatesCount certificados", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PopularCourseCard(course: Course, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f)

    Card(
        modifier = Modifier
            .width(220.dp)
            .scale(scale)
            .clickable(onClick = onClick, onClickLabel = course.title),
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp
    ) {
        Column {
            if (course.coverRes != null) {
                Image(
                    painter = painterResource(id = course.coverRes),
                    contentDescription = course.title,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(Primary, PrimaryVariant))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Outlined.Code, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                AnimatedVisibility(visible = course.author.isNotEmpty()) {
                    Text(
                        text = "Por ${course.author}",
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, icon: Any, brush: Brush, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), elevation = 4.dp) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(brush),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .padding(12.dp)) {
                when (icon) {
                    is ImageVector -> Icon(imageVector = icon, contentDescription = title, tint = Primary)
                    else -> Image(painter = icon as androidx.compose.ui.graphics.painter.Painter, contentDescription = title)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold)
        }
    }
}
