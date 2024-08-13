plugins {
    id("compose.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.library"
}

dependencies {
    implementation(projects.core.compose)
    implementation(projects.core.utils)
    implementation(projects.core.background)
    implementation(projects.data.db)
    implementation(projects.data.parsing)
    implementation(projects.data.models)

    implementation(projects.features.chapters)
    implementation(projects.features.catalog)
    implementation(projects.features.categories)
    implementation(projects.features.statistic)
    implementation(projects.features.storage)
    implementation(projects.features.settings)
    implementation(projects.features.schedule)
//    implementation(projects.features.accounts.main)

    api(projects.ksp)
    ksp(projects.ksp)
}
