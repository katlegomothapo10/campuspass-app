plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.campuspass.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.campuspass.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // =========================
    // JETPACK COMPOSE
    // =========================

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // =========================
    // RETROFIT / API
    // =========================

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // =========================
    // VIEWMODEL
    // =========================

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // =========================
    // DATASTORE
    // =========================

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // =========================
    // NAVIGATION
    // =========================

    implementation("androidx.navigation:navigation-compose:2.8.4")

    // =========================
    // GOOGLE SIGN-IN
    // =========================

    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // =========================
    // BIOMETRIC AUTHENTICATION
    // =========================

    implementation("androidx.biometric:biometric:1.1.0")

    // =========================
    // ROOM DATABASE
    // =========================

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // =========================
    // QR CODE SCANNING - ZXING
    // =========================

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // =========================
    // TESTING
    // =========================

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
}