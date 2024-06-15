plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.storage"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.data.db)
    implementation(projects.data.models)
}
