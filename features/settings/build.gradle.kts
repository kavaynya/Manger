plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.settings"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.data.db)
    implementation(projects.data.models)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
