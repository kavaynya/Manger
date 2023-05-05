plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.core.compose"
}

dependencies {
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.internet))

    implementation(platform(libs.compose.bom))

    api(libs.bundles.compose)
    api(libs.bundles.accompanist)
    api(libs.collections.immutable)

    api(libs.core)
    api(libs.activity)
}
