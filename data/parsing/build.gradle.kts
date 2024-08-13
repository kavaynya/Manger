plugins {
    id("base.library")
}

android {
    namespace = "com.san.kir.data.parsing"
}

dependencies {
    implementation(projects.core.internet)
    implementation(projects.core.utils)
    implementation(projects.data.models)

    implementation(libs.coroutines.core)
}
