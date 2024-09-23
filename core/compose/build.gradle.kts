plugins {
    id("compose.library")
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
}
