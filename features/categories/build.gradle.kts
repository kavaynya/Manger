plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.categories"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.data.db)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
