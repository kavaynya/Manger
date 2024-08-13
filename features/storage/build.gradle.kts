plugins {
    id("compose.library")
    `kotlin-parcelize`
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.storage"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.data.db)
    implementation(projects.data.models)

    api(projects.ksp)
    ksp(projects.ksp)
}
