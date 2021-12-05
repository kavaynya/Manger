plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.PARCELIZE)
}

android {
    compileSdk = Versions.App.COMPILE_SDK

    defaultConfig {
        minSdk = Versions.App.MIN_SDK
        targetSdk = Versions.App.TARGET_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = Versions.JAVA
        targetCompatibility = Versions.JAVA
    }

    prepareKotlinOptions()
}

dependencies {
    implementation(project(Modules.Core.support))
    implementation(project(Modules.Core.utils))
//
//    implementation(Dependencies.INJECT)

    Dependencies.AndroidX.Room.apply {
//        ksp(ROOM_COMPILER)
//        implementation(ROOM_RUNTIME)
        implementation(ROOM_KTX)
//        implementation(ROOM_PAGING)
    }

//    Dependencies.AndroidX.Lifecycle.apply {
//        implementation(LIFECYCLE_LIVEDATA)
//    }
//
//    Dependencies.AndroidX.apply {
//        implementation(PAGING)
//    }

}
