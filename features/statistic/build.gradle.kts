plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.statistic"
}

dependencies {
    implementation(project(Modules.Core.compose))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Data.db))
    implementation(project(Modules.Data.models))

    api(project(Modules.ksp))
    ksp(project(Modules.ksp))
}
