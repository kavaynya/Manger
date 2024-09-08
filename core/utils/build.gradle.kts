plugins {
    id("compose.library")
    `kotlin-parcelize`
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.san.kir.core.utils"
}

dependencies {
    implementation(libs.compose.runtime)
    implementation(libs.compose.runtime.saveable)
    implementation(libs.compose.ui)
    implementation(libs.stdlib)

    api(libs.serialization)
    api(libs.bundles.coroutines)
    api(libs.bundles.decompose)
    api(libs.timber)
    api(libs.datetime)
}
