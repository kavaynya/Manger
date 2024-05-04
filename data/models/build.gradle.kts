plugins {
    id("compose.library")
    id(Plugins.parcelize)
}

android {
    namespace = "com.san.kir.data.models"
}

dependencies {
    implementation(project(Modules.Core.utils))

    implementation(libs.compose.material.icons)
    implementation(libs.compose.material.icons.ext)
    implementation(libs.datetime)
}
