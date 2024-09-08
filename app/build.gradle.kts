plugins {
    id("compose.app")

    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.san.kir.manger"

    defaultConfig {
        applicationId = "com.san.kir.manger"

        versionCode = 283
        versionName = "2.8.3"

        setProperty("archivesBaseName", "Manger $versionName")
    }

    //    flavorDimensions += "version"
    //    productFlavors {
    //        create("r") {
    //            dimension = "version"
    //            applicationIdSuffix = ""
    //            versionNameSuffix = ""
    //        }
    //        create("alpha") {
    //            dimension = "version"
    //            applicationIdSuffix = ".alpha"
    //            versionNameSuffix = "-alpha"
    //        }
    //    }
}

dependencies {
    implementation(projects.features.library)

    implementation(projects.core.utils)
    implementation(projects.core.internet)
    implementation(projects.core.compose)
    implementation(projects.core.background)

    implementation(projects.data.db)
    implementation(projects.data.models)

    implementation(projects.ksp)
    ksp(projects.ksp)

    implementation(libs.activity)
    implementation(libs.appcompat)
    implementation(libs.vectordrawable)

    //    debugImplementation(libs.bundles.hyper)

    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.tests)
    debugImplementation(libs.compose.manifest)

    //        androidTestImplementation(TRUTH)
    //        androidTestImplementation(BENCHMARK_JUNIT)
    //        androidTestImplementation(NAVIGATION)
    //    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")

    // Use the most recent version of Compose available.
    // debugImplementation 'org.jetbrains.kotlin:kotlin-reflect:1.5.20'
}
