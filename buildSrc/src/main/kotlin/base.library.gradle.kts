plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = Versions.App.COMPILE_SDK

    defaultConfig {
        minSdk = Versions.App.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }

    kotlinOptions {
        jvmTarget = "${Versions.JAVA}"
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + project.buildDir.absolutePath + "/compose_metrics"
        )
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + project.buildDir.absolutePath + "/compose_metrics"
        )
    }

    kotlin {
        jvmToolchain(17)

        buildTypes.onEach { variant ->
            sourceSets {
                getByName(variant.name) {
                    kotlin.srcDir(layout.buildDirectory.dir("/generated/ksp/${variant.name}/kotlin"))
                }
            }
        }
    }
}
