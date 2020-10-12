plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.4.10"
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")
    id("io.gitlab.arturbosch.detekt") version "1.14.1"
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
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.fragment:fragment-ktx:1.3.0-beta01")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.2")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.room:room-ktx:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")

    implementation("com.google.firebase:firebase-crashlytics-ktx:17.2.2")
    implementation("com.google.firebase:firebase-analytics-ktx:17.6.0")

    implementation("com.google.android.gms:play-services-auth:18.1.0")

    implementation("com.google.dagger:hilt-android:2.29.1-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.29.1-alpha")

    implementation("io.arrow-kt:arrow-core-data:0.11.0")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("com.github.bumptech.glide:glide:4.11.0")

    implementation("com.google.android.exoplayer:exoplayer-core:2.12.0")
    implementation("com.google.android.exoplayer:extension-okhttp:2.12.0")
    implementation("com.google.android.exoplayer:extension-leanback:2.12.0")
}
