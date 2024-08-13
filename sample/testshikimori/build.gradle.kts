plugins {
    id("base.app")
}

android {
    namespace = "com.san.kir.testshikimori"
    defaultConfig {
        applicationId = "com.san.kir.testshikimori"

        versionCode = 1
        versionName = "1.0"

        setProperty("archivesBaseName", "Test Shikimori $versionName")
    }
}

dependencies {
    implementation(projects.features.shikimori)
    implementation(projects.data.db)
    implementation(projects.core.utils)

    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.vectordrawable)
    implementation(libs.constraintlayout)

    implementation(libs.material)

}
