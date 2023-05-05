plugins {
    id("base.library")
    alias(libs.plugins.kotlin.ksp)
    id(Plugins.parcelize)
}

android {
    namespace = "com.san.kir.data.db"
    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            allowSourcesFromOtherPlugins = true
        }
    }
}

dependencies {
    implementation(project(Modules.Data.models))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.support))

    implementation(platform(libs.compose.bom))

    ksp(libs.room.compiler)
    api(libs.room.runtime)
    implementation(libs.bundles.room)

    implementation(libs.paging)
    implementation(libs.compose.runtime)

    implementation(libs.gson)
    implementation(libs.timber)
}
