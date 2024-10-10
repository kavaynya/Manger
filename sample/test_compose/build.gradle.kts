plugins {
    id("compose.app")
}

android {
    namespace = "com.san.kir.test_compose"
    defaultConfig {
        applicationId = "com.san.kir.test_compose"

        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(libs.activity)
    implementation(projects.core.compose)
    implementation(projects.core.utils)
}
