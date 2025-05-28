plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt") // Esto es importante
}

android {
    namespace = "com.example.proyecto_agenda"
    compileSdk = 34
    kotlinOptions {
        jvmTarget = "1.8"
    }
    defaultConfig {
        applicationId = "com.example.proyecto_agenda"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.airbnb.android:lottie:5.0.1")
    implementation("com.google.firebase:firebase-analytics:20.0.0")
    testImplementation(libs.junit)
    implementation("com.google.android.material:material:1.5.0")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-database:19.6.0")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("com.google.code.gson:gson:2.8.8")

    // ROOM
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")

    kapt("androidx.room:room-compiler:$room_version") // Para kapt

    // Soporte para Kotlin Extensions y Coroutines
    implementation("androidx.room:room-ktx:$room_version")

    // Soporte opcional para RxJava2
    implementation("androidx.room:room-rxjava2:$room_version")

    // Soporte opcional para RxJava3
    implementation("androidx.room:room-rxjava3:$room_version")

    // Soporte opcional para Guava
    implementation("androidx.room:room-guava:$room_version")

    // Helpers para pruebas opcionales
    testImplementation("androidx.room:room-testing:$room_version")

    // Integración opcional con Paging 3
    implementation("androidx.room:room-paging:$room_version")

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
