plugins {
    id("base.library")
    alias(libs.plugins.kotlin.ksp)
    `kotlin-parcelize`
}

android {
    namespace = "com.san.kir.data.db"
    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            allWarningsAsErrors = true
            allowSourcesFromOtherPlugins = true
        }
    }
}

dependencies {
    implementation(projects.data.models)
    implementation(projects.core.utils)

    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
}
