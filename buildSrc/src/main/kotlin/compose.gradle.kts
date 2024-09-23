androidConfig {
    plugins {
        alias(libs.plugins.compose)
    }
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)
}
