plugins {
    id("base.library")
}

android {
    namespace = "com.san.kir.background"
}

dependencies {
    implementation(projects.data.models)
    implementation(projects.core.utils)
    implementation(projects.core.internet)
    implementation(projects.data.db)
    implementation(projects.data.parsing)

    implementation(libs.core)

    api(libs.work.runtime)
    implementation(libs.work.gcm)
    implementation(libs.work.multiprocess)
}
