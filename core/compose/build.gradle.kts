plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.san.kir.core.compose"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.internet)
    implementation(libs.activity)

    api(libs.bundles.compose)
    api(libs.bundles.accompanist)
}
