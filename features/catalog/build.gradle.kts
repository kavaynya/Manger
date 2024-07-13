plugins {
    id("compose.library")

    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace="com.san.kir.catalog"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.core.internet)
    implementation(projects.data.db)
    implementation(projects.data.models)
    implementation(projects.data.parsing)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
