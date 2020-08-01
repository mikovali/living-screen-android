buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath(kotlin("gradle-plugin", version = "1.3.72"))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.28.3-alpha")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
