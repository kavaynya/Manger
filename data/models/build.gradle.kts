plugins {
    id("compose.library")
    `kotlin-parcelize`
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.san.kir.data.models"
}

dependencies {
    implementation(projects.core.utils)

    implementation(libs.compose.material.icons)
    implementation(libs.compose.material.icons.ext)
}
