plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.statistic"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.data.db)

    api(projects.ksp)
    ksp(projects.ksp)
}
