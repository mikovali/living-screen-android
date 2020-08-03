plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.3.72"
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.sensorfields.livingscreen"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0.0"
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", "true")
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

dependencies {
    implementation(kotlin("stdlib"))

    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha07")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.room:room-ktx:2.2.5")
    kapt("androidx.room:room-compiler:2.2.5")

    implementation("com.google.firebase:firebase-auth-ktx:19.3.2")
    implementation("com.google.android.gms:play-services-auth:18.1.0")

    implementation("com.google.dagger:hilt-android:2.28.3-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.28.3-alpha")

    implementation("io.arrow-kt:arrow-core-data:0.10.5")

    implementation("com.squareup.okhttp3:logging-interceptor:4.8.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.5.0")
}
