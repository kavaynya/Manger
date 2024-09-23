import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("com.android.library")
    id("basic")
}

kotlin {
    explicitApi = ExplicitApiMode.Strict
}


android {
    defaultConfig {
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

    kotlinOptions {
        applyProjectConfigurations()
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
