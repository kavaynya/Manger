plugins {
    id("compose.library")

    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.features.accounts.main"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.internet)
    implementation(projects.data.parsing)
    implementation(projects.features.shikimori)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
