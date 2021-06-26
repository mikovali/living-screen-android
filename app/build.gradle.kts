plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Versions.kotlin
    kotlin("plugin.parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")
    id("io.gitlab.arturbosch.detekt") version Versions.detekt
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
            javacOptions {
                // These options are normally set automatically via the Hilt Gradle plugin, but we
                // set them manually to workaround a bug in the Kotlin 1.5.20
                option("-Adagger.fastInit=ENABLED")
                option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
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
            "-Xopt-in=kotlin.ExperimentalStdlibApi"
        )
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

detekt {
    buildUponDefaultConfig = true
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.androidDesugar}")

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}")

    implementation("androidx.fragment:fragment-ktx:${Versions.fragment}")
    implementation("androidx.navigation:navigation-fragment-ktx:${Versions.navigation}")
    implementation("androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}")
    implementation("androidx.leanback:leanback:${Versions.leanback}")
    implementation("androidx.leanback:leanback-paging:${Versions.leanbackPaging}")
    implementation("androidx.paging:paging-common-ktx:${Versions.paging}")
    implementation("androidx.paging:paging-runtime-ktx:${Versions.paging}")
    implementation("androidx.room:room-ktx:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")

    implementation("com.google.firebase:firebase-crashlytics-ktx:${Versions.crashlytics}")
    implementation("com.google.firebase:firebase-analytics-ktx:${Versions.analytics}")

    implementation("com.google.android.gms:play-services-auth:${Versions.playServicesAuth}")

    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    implementation("com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}")
    implementation("com.squareup.retrofit2:retrofit:${Versions.retofit}")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.serializationConverter}")

    implementation("com.jakewharton.timber:timber:${Versions.timber}")
    implementation("io.arrow-kt:arrow-core-data:${Versions.arrow}")
    implementation("io.coil-kt:coil:${Versions.coil}")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    implementation("com.google.android.exoplayer:exoplayer-core:${Versions.exoplayer}")
    implementation("com.google.android.exoplayer:extension-okhttp:${Versions.exoplayer}")
    implementation("com.google.android.exoplayer:extension-leanback:${Versions.exoplayer}")
}
