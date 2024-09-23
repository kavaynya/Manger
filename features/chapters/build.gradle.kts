plugins {
    id("compose.library")
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
    implementation(projects.features.viewer)
    implementation(projects.features.catalog)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
