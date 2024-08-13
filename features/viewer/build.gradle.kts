plugins {
    id("base.library")
    `kotlin-parcelize`
}

android {
    namespace = "com.san.kir.features.viewer"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(projects.data.models)
    implementation(projects.data.db)
    implementation(projects.data.parsing)
    implementation(projects.core.internet)
    implementation(projects.core.utils)

    implementation(libs.subsampling)

    implementation(libs.core)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.appcompat)

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.common)

    implementation(libs.material)
}
