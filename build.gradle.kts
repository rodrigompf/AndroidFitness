plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

val cardstackviewVersion by extra("2.3.4")  // Ensure you use the correct version

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        // Add the Google Services classpath for Firebase integration
        classpath("com.google.gms:google-services:4.3.15") // Firebase services plugin
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.0") // Optional, if you use Crashlytics
    }
}

