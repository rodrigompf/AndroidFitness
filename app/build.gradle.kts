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
    // Firebase BOM: Use BOM to manage Firebase dependency versions
    implementation(platform("com.google.firebase:firebase-bom:32.2.0"))

    // Firebase services: Versions managed by BOM
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    //Zoom
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")

    // Material Components
    implementation("com.google.android.material:material:1.9.0")

    // AndroidX libraries
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Image Loading with Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.androidx.activity)
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Optional: Card Stack View
    implementation("com.github.yuyakaido:CardStackView:2.3.4")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}


// Apply Google services plugin
apply(plugin = "com.google.gms.google-services")