plugins {
    id("compose.library")
    id(Plugins.kapt)
    id(Plugins.hilt)
}

android {
    namespace="com.san.kir.library"
}

dependencies {
    implementation(project(Modules.Core.composeUtils))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.support))
    implementation(project(Modules.Core.background))
    implementation(project(Modules.Data.db))
    implementation(project(Modules.Data.parsing))

    implementation(libs.timber)

    implementation(libs.collections.immutable)

    implementation(libs.lifecycle.livedata)

    implementation(libs.compose.hilt.navigation)
    implementation(libs.hilt.android)
    kapt(libs.bundles.hiltCompilers)

}
