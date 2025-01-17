import Dependencies.common

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Versions.App.COMPILE_SDK)

    defaultConfig {
        minSdkVersion(Versions.App.MIN_SDK)
        targetSdkVersion(Versions.App.TARGET_SDK)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }

    kotlinOptions()
}

dependencies {
    common()
}

//plugins {
//    id(Plugins.COMMON)
//}
//
//dependencies {
//    implementation(Dependencies.Google.MATERIAL)
//}
