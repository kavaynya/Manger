plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.san.kir.core.compose"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.internet))
    implementation(libs.activity)

    api(libs.bundles.compose)
    api(libs.bundles.accompanist)
}
