plugins {
    id("compose.library")
    `kotlin-parcelize`
}

android {
    namespace = "com.san.kir.core.utils"
}

dependencies {
    implementation(libs.stdlib)
}
