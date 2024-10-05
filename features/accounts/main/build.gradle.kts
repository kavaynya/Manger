plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.features.accounts.main"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.internet)
    implementation(projects.data.parsing)
    implementation(projects.features.accounts.shikimori)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
