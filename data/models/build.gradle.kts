plugins {
    id("compose.library")
    `kotlin-parcelize`
}

android {
    namespace = "com.san.kir.data.models"
}

dependencies {
    implementation(projects.core.utils)
}
