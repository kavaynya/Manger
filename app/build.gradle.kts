plugins {
    id("compose.app")
}

android {
    namespace = "com.san.kir.manger"

    defaultConfig {
        applicationId = "com.san.kir.manger.alpha"

        versionCode = 300
        versionName = "3.0.0-alpha"

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
