plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.settings"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.data.db)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
