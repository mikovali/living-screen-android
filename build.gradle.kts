buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
        classpath(kotlin("gradle-plugin", version = "1.4.10"))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.29.1-alpha")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.3.0")
        classpath("com.google.firebase:firebase-appdistribution-gradle:2.0.1")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
