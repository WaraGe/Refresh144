plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ukbuild.refresh144"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ukbuild.refresh144"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
