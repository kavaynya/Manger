plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.san.kir.core.compose"
}

dependencies {
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.internet))
    implementation(libs.core)

    api(libs.bundles.compose)
    api(libs.bundles.accompanist)
}
