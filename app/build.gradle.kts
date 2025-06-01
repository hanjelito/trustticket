plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "es.polizia.trustticket"
    compileSdk = 35

    defaultConfig {
        applicationId = "es.polizia.trustticket"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // constraint layout
    implementation(libs.constraint.layout)
    // navigation
    implementation("androidx.navigation:navigation-compose:2.9.0")
    //imagenes intetnet
    implementation("io.coil-kt:coil-compose:2.6.0")
    // read json gson
    implementation("com.google.code.gson:gson:2.10.1")
    // lifecycle view model es para el manejo de estados
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    // room entity es para la persistencia de datos
    implementation("androidx.room:room-runtime:2.7.1")
    // room ktx es para la persistencia de datos
    implementation("androidx.room:room-ktx:2.7.1")
    //
    // --- 1) Retrofit y Gson Converter (para llamadas HTTP/REST) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // --- 2) OkHttp (complementario a Retrofit) ---
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    // (opcional) Logging interceptor para depuración:
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // --- 3) Coroutines (para ejecutar llamadas en background) ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // --- 4) DataStore Preferences (opcional, para guardar el token) ---
    implementation("androidx.datastore:datastore-preferences:1.1.0")

    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

}