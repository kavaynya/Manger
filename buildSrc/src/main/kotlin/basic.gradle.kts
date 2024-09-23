import gradle.kotlin.dsl.accessors._9ba3a12095c0a769845b22313807cf44.implementation

plugins {
    kotlin("android")
}

androidConfig {

    plugins {
        alias(libs.plugins.serialization)
        alias(libs.plugins.kotlin.ksp)
    }

    compileSdk = libs.versions.complileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = projectJavaVersion
        targetCompatibility = projectJavaVersion
    }

}

dependencies {
    implementation(libs.serialization)
    implementation(libs.timber)
    implementation(libs.datetime)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.decompose)
}
