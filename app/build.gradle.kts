plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    kotlin("plugin.serialization") version Versions.kotlin
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
}

android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.sensorfields.livingscreen"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0.0${property("appVersionNameSuffix")}"
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", "true")
            }
        }
    }
    signingConfigs {
        maybeCreate("debug").apply {
            storeFile = File(projectDir, "debug.keystore")
        }
    }
    buildTypes {
        maybeCreate("debug").apply {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs["debug"]
            firebaseAppDistribution {
                groups = "debug"
            }
        }
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

androidExtensions {
    features = setOf("parcelize")
}

detekt {
    buildUponDefaultConfig = true
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")

    implementation("androidx.fragment:fragment-ktx:1.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.navigation}")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.leanback:leanback:1.1.0-beta01")
    implementation("androidx.leanback:leanback-paging:1.1.0-alpha07")
    implementation("androidx.paging:paging-common-ktx:${Versions.paging}")
    implementation("androidx.paging:paging-runtime-ktx:${Versions.paging}")
    implementation("androidx.room:room-ktx:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")

    implementation("com.google.firebase:firebase-crashlytics-ktx:17.3.1")
    implementation("com.google.firebase:firebase-analytics-ktx:18.0.2")

    implementation("com.google.android.gms:play-services-auth:19.0.0")

    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    implementation("io.arrow-kt:arrow-core-data:0.11.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("com.github.bumptech.glide:glide:4.11.0")

    implementation("com.google.android.exoplayer:exoplayer-core:${Versions.exoplayer}")
    implementation("com.google.android.exoplayer:extension-okhttp:${Versions.exoplayer}")
    implementation("com.google.android.exoplayer:extension-leanback:${Versions.exoplayer}")
}
