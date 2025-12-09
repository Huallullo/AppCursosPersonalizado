// app/build.gradle.kts
// Módulo app con Jetpack Compose y Firebase Analytics
// Reemplaza `com.tuempresa.cursoscompose` por tu package real registrado en Firebase.

plugins {
    // Usamos los aliases definidos en version catalog (libs.versions.toml)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // El plugin de Google Services fue declarado en el build raíz con apply false,
    // aquí lo aplicamos para que procese google-services.json
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tuempresa.cursoscompose" // <-- REEMPLAZA por tu paquete
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tuempresa.cursoscompose" // <-- REEMPLAZA por tu paquete
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // Habilitar Compose
    buildFeatures {
        compose = true
    }

    // Si tienes alias para la versión del compiler en tu catalog, sustitúyelo.
    composeOptions {
        // Si usas version catalog: replace property with libs.versions.compose.kotlinCompilerExtension.get()
        kotlinCompilerExtensionVersion = "1.6.10" // <- ajusta si tu proyecto usa otra versión
    }
    compileOptions {
        // Forzar Java 17 para el compilador Java
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Opciones de Kotlin
    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            // Evitar conflictos en empaquetado
            excludes += setOf("META-INF/DEPENDENCIES", "META-INF/LICENSE")
        }
    }
}

dependencies {
    // BOM de Compose (opcional si manejas con version catalog)
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    // Core Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.0")

    // Coil para cargar imágenes en Compose (opcional)
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Firebase Analytics (KTX)
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")

    // (Opcional) Firebase Auth / Firestore si quieres integrar después
    // implementation("com.google.firebase:firebase-auth-ktx:22.1.1")
    // implementation("com.google.firebase:firebase-firestore-ktx:24.6.0")

    // Debugging / herramientas
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Tests (opcional)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Coroutine + lifecycle + ViewModel Compose
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

// Optional: Firestore (solo si luego la activas)
    implementation("com.google.firebase:firebase-firestore-ktx:24.6.0")

    implementation("com.google.firebase:firebase-firestore-ktx:24.6.0")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
// Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.6.0")

// Firebase Auth (para signInAnonymously en pruebas)
    implementation("com.google.firebase:firebase-auth-ktx:22.1.1")

// Analytics (si aún no está)
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")

// Coroutines (si no están)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.6.0")

}
