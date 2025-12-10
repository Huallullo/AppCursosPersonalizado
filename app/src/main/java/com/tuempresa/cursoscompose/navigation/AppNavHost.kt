package com.tuempresa.cursoscompose.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuempresa.cursoscompose.R
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.ui.screens.BottomBarScreen
import com.tuempresa.cursoscompose.ui.screens.CourseDetailScreen
import com.tuempresa.cursoscompose.ui.screens.LoginAuthScreen
import com.tuempresa.cursoscompose.ui.screens.ProgressScreen
import com.tuempresa.cursoscompose.ui.screens.HomeScreen
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppNavHost(
    vm: CoursesViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val courses by vm.courses.collectAsState()
    val progress by vm.progress.collectAsState()
    val certificatesCount = progress.flatMap { it.certificates }.size

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginAuthScreen(navController = navController)
        }

        composable(BottomBarScreen.Home.route) {
            HomeScreen(
                popularCourses = courses.take(4),
                certificatesCount = certificatesCount,
                onCourseClick = { courseId -> navController.navigate("detail/$courseId") },
                onCatalogClick = { navController.navigate(BottomBarScreen.Catalog.route) },
                onProfileClick = { navController.navigate(BottomBarScreen.Profile.route) },
                onCertificatesClick = { navController.navigate("certificates") }
            )
        }

        composable(BottomBarScreen.Catalog.route) {
            CatalogScreen(courses = courses, onOpenDetail = { courseId -> navController.navigate("detail/$courseId") })
        }

        composable(BottomBarScreen.Profile.route) {
            ProgressScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onMyCoursesClick = { navController.navigate("my-courses") },
                onAchievementsClick = { navController.navigate("achievements") },
                onCertificatesClick = { navController.navigate("certificates") },
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo(BottomBarScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable("detail/{courseId}") { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val course = courses.find { it.id == courseId } ?: Course(id = courseId, title = "Curso no encontrado")
            CourseDetailScreen(course = course, onBack = { navController.popBackStack() })
        }

        composable("my-courses") { MyCoursesScreen(vm, navController::popBackStack) }
        composable("achievements") { AchievementsScreen(navController::popBackStack) }
        composable("certificates") { CertificatesScreen(progress.flatMap { it.certificates }, navController::popBackStack) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogScreen(courses: List<Course>, onOpenDetail: (String) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Todos los Cursos") }, backgroundColor = Color.Transparent, elevation = 0.dp) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(courses, key = { it.id }) { course ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenDetail(course.id) }.animateItem(),
                    elevation = 6.dp,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(MaterialTheme.colors.primary.copy(alpha = 0.06f), MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            if (course.coverRes != null) {
                                Image(painter = painterResource(id = course.coverRes), contentDescription = null, modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)))
                            } else {
                                Icon(imageVector = Icons.Outlined.Code, contentDescription = null, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(course.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                            AnimatedVisibility(visible = course.author.isNotEmpty()) {
                                Text("Por ${course.author}", style = MaterialTheme.typography.body2, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyCoursesScreen(vm: CoursesViewModel, onBack: () -> Unit) {
    val courses by vm.courses.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Cursos") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Atrás") } }, backgroundColor = Color.Transparent, elevation = 0.dp) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val inProgressCourses = courses.filter { it.progressPercent in 1..99 }
            if (inProgressCourses.isEmpty()) {
                item { Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("Aún no estás cursando nada.") } }
            } else {
                items(inProgressCourses, key = { it.id }) { course ->
                    Card(modifier = Modifier.fillMaxWidth().animateItem()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(course.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(progress = course.progressPercent / 100f, modifier = Modifier.fillMaxWidth())
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("${course.progressPercent}%")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Logros") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Atrás") } }, backgroundColor = Color.Transparent, elevation = 0.dp) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Usar iconos Material en lugar de recursos vectoriales externos para evitar referencias no encontradas
            item { AchievementItem("Primer curso completado", Icons.Outlined.EmojiEvents, Color(0xFFD7A200)) }
            item { AchievementItem("Racha de 3 días seguidos", Icons.Outlined.EmojiEvents, Color(0xFFE25B0B)) }
            item { AchievementItem("10 lecciones finalizadas", Icons.Outlined.EmojiEvents, Color(0xFF2E7D32)) }
            item { AchievementItem("Primer curso de IA iniciado", Icons.Outlined.EmojiEvents, Color(0xFF0277BD)) }
        }
    }
}

@Composable
fun AchievementItem(text: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.body1)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CertificatesScreen(certificates: List<String>, onBack: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Certificados") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Atrás") } }, backgroundColor = Color.Transparent, elevation = 0.dp) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (certificates.isEmpty()) {
                item { Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("Completa un curso al 100% para obtener tu primer certificado.") } }
            } else {
                items(certificates, key = { it }) { certificate ->
                    Card(modifier = Modifier.fillMaxWidth().animateItem(), elevation = 2.dp, shape = RoundedCornerShape(12.dp)) {
                        Text(certificate, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
