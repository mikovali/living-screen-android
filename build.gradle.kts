buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePlugin}")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")
        classpath("com.google.gms:google-services:${Versions.googleServices}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsPlugin}")
        classpath("com.google.firebase:firebase-appdistribution-gradle:${Versions.appdistribution}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
