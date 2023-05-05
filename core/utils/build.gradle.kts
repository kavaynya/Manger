plugins {
    id("compose.library")
    id(Plugins.kotlin)
}

android {
    namespace = "com.san.kir.core.utils"
}

dependencies {
    implementation(project(Modules.Core.support))

    implementation(libs.stdlib)

    api(libs.collections.immutable)
    api(libs.bundles.coroutines)
    api(libs.bundles.decompose)
    api(libs.timber)

    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.lifecycle.viewmodel)
}
