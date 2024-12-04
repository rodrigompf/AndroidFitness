plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)  // Apply the Google Services plugin
}

android {
    namespace = "com.example.androidfitness"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidfitness"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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
}

dependencies {
    implementation("com.google.android.material:material:1.8.0") // Material design
    implementation("androidx.appcompat:appcompat:1.7.0") // Backward compatibility
    implementation(libs.androidx.core.ktx) // AndroidX Core KTX
    implementation(libs.androidx.appcompat) // AndroidX AppCompat
    implementation(libs.material) // Material components
    implementation(libs.androidx.activity) // AndroidX Activity
    implementation(libs.androidx.constraintlayout) // ConstraintLayout
    implementation(libs.firebase.firestore) // Firestore dependency
    implementation("com.google.firebase:firebase-auth:21.1.0") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore:25.1.1") // Firestore dependency
    implementation("com.github.bumptech.glide:glide:4.15.1") // Glide for image loading
    implementation("androidx.recyclerview:recyclerview:1.2.1") // RecyclerView for lists
    implementation("com.github.yuyakaido:CardStackView:2.3.4") // Card Stack View (optional)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // Glide compiler
    testImplementation(libs.junit) // JUnit for testing
    androidTestImplementation(libs.androidx.junit) // AndroidX JUnit
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing
}

// Apply Google services plugin
apply(plugin = "com.google.gms.google-services")