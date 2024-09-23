plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.features.accounts.shikimori"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.data.db)
    implementation(projects.core.compose)
    implementation(projects.core.internet)
    implementation(projects.features.catalog)

    implementation(libs.okhttp.loging)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.common)

    implementation(libs.datastore)

    implementation(projects.ksp)
    ksp(projects.ksp)
}
