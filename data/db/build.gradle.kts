plugins {
    id("base.library")
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
    implementation(projects.core.utils)
    api(projects.data.models)

    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
}
