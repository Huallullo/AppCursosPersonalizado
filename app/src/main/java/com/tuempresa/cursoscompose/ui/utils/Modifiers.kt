package com.tuempresa.cursoscompose.ui.utils

import androidx.compose.animation.animateContentSize
import androidx.compose.ui.Modifier

// Extensión mínima para evitar referencias faltantes a `animateItem()` desde la UI.
// Aplica una animación de tamaño al elemento; se puede extender más adelante.
fun Modifier.animateItem(): Modifier = this.animateContentSize()

