plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.chapters"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.compose)
    implementation(projects.core.background)
    implementation(projects.core.internet)
    implementation(projects.data.db)
    implementation(projects.data.parsing)
    implementation(projects.data.models)
    implementation(projects.features.viewer)
    implementation(projects.features.catalog)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
