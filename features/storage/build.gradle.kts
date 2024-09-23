plugins {
    id("compose.library")
    `kotlin-parcelize`
}

android {
    namespace = "com.san.kir.storage"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.data.db)

    api(projects.ksp)
    ksp(projects.ksp)
}
