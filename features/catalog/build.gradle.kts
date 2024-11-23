plugins {
    id("compose.library")
}

android {
    namespace="com.san.kir.catalog"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.core.internet)
    implementation(projects.data.db)
    implementation(projects.data.parsing)

    implementation(libs.datastore)
    implementation(projects.ksp)
    ksp(projects.ksp)
}
