plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.schedule"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.data.db)
    implementation(projects.data.parsing)

    implementation(libs.activity)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
