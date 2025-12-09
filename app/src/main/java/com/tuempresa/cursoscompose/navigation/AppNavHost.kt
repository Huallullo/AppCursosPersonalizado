package com.tuempresa.cursoscompose.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.sample.SampleData
import com.tuempresa.cursoscompose.ui.screens.CourseDetailScreen
import com.tuempresa.cursoscompose.ui.screens.ProgressScreen
import com.tuempresa.cursoscompose.viewmodel.CoursesViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(start: String = "catalog") {
    val navController = rememberNavController()
    val vm: CoursesViewModel = viewModel()

    NavHost(navController = navController, startDestination = start) {
        composable("catalog") {
            CatalogInline(vm = vm,
                onOpenDetail = { courseId -> navController.navigate("detail/$courseId") },
                onOpenProgress = { navController.navigate("progress") }
            )
        }

        composable(
            "detail/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val course: Course = vm.courses.value.firstOrNull { it.id == courseId }
                ?: SampleData.sampleCourses.firstOrNull()
                ?: Course(id = courseId, title = "Curso", author = "Autor")
            CourseDetailScreen(course = course, viewModel = vm, onBack = { navController.popBackStack() })
        }

        composable("progress") {
            ProgressScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun CatalogInline(
    vm: CoursesViewModel,
    onOpenDetail: (String) -> Unit,
    onOpenProgress: () -> Unit
) {
    val courses by vm.courses.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var authorInput by remember { mutableStateOf("") }
    var confirmDeleteFor by remember { mutableStateOf<String?>(null) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                actions = {
                    TextButton(onClick = onOpenProgress) { Text("Progreso") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { showDialog = true }) { Text("Crear") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(12.dp)) {
            if (courses.isEmpty()) {
                Text("No hay cursos disponibles", style = MaterialTheme.typography.body1)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(courses) { c ->
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                        ) {
                            Row(modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier
                                    .weight(1f)
                                    .clickable { onOpenDetail(c.id) }) {
                                    Text(c.title, style = MaterialTheme.typography.subtitle1)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Por ${c.author}", style = MaterialTheme.typography.body2)
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("${c.progressPercent}%")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { confirmDeleteFor = c.id }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para crear curso — aquí usamos scope.launch dentro de callback (válido)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Crear nuevo curso") },
            text = {
                Column {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = authorInput,
                        onValueChange = { authorInput = it },
                        label = { Text("Autor") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (titleInput.isBlank() || authorInput.isBlank()) {
                        scope.launch { scaffoldState.snackbarHostState.showSnackbar("Ingrese título y autor") }
                        return@TextButton
                    }
                    vm.addCourseToCatalog(titleInput.trim(), authorInput.trim()) { ok, idOrMsg ->
                        // callback puede ejecutarse fuera del contexto composable — usamos scope.launch para UI
                        scope.launch {
                            if (ok) {
                                scaffoldState.snackbarHostState.showSnackbar("Curso creado (id=$idOrMsg)")
                            } else {
                                scaffoldState.snackbarHostState.showSnackbar("Error: ${idOrMsg ?: "desconocido"}")
                            }
                        }
                    }
                    titleInput = ""
                    authorInput = ""
                    showDialog = false
                }) {
                    Text("Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Dialog de confirmación para borrar
    confirmDeleteFor?.let { courseId ->
        AlertDialog(
            onDismissRequest = { confirmDeleteFor = null },
            title = { Text("Eliminar curso") },
            text = { Text("¿Seguro que quieres eliminar este curso? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteCourse(courseId) { ok, msg ->
                        scope.launch {
                            if (ok) scaffoldState.snackbarHostState.showSnackbar("Curso eliminado")
                            else scaffoldState.snackbarHostState.showSnackbar("Error al eliminar: ${msg ?: "desconocido"}")
                        }
                    }
                    confirmDeleteFor = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteFor = null }) { Text("Cancelar") }
            }
        )
    }
}
