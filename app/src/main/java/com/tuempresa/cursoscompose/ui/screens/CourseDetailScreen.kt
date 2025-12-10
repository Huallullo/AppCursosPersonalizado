package com.tuempresa.cursoscompose.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.cursoscompose.model.Course
import com.tuempresa.cursoscompose.model.Lesson
import com.tuempresa.cursoscompose.ui.theme.Primary
import com.tuempresa.cursoscompose.ui.theme.PrimaryVariant

@Composable
fun CourseDetailScreen(course: Course, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(course.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Atrás"
                        )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 6.dp
                ) {
                    Column {
                        // Portada del curso
                        if (course.coverRes != null) {
                            Image(
                                painter = painterResource(id = course.coverRes),
                                contentDescription = course.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .height(160.dp)
                                    .fillMaxWidth()
                                    .background(Brush.linearGradient(listOf(Primary, PrimaryVariant))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(course.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Por ${course.author}", fontSize = 14.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(imageVector = Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(course.difficulty, fontSize = 14.sp, color = Color.Gray)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { /* continuar lógica */ }, modifier = Modifier.weight(1f)) {
                                    Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Continuar")
                                }
                                OutlinedButton(onClick = { /* guardar */ }) {
                                    Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }

            item { Text("Programa", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold) }

            items(course.lessons) { lesson ->
                LessonItem(lesson = lesson)
            }
        }
    }
}

@Composable
private fun LessonItem(lesson: Lesson) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { /* abrir lección */ }
    , elevation = 2.dp, shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Outlined.PlayArrow, contentDescription = null, tint = Primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(lesson.title, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(progress = lesson.progress / 100f, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("${lesson.progress}%", color = Color.Gray)
        }
    }
}
