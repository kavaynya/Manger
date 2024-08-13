plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.statistic"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.data.db)
    implementation(projects.data.models)

    api(projects.ksp)
    ksp(projects.ksp)
}
