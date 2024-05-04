plugins {
    id("base.library")
}

android {
    namespace = "com.san.kir.core.internet"
}

dependencies {
    implementation(project(Modules.Core.utils))

    implementation(libs.coroutines.core)
    implementation(libs.stdlib)

    api(libs.jsoup)
    api(libs.okio)
    api(libs.okhttp)
    implementation(libs.okhttp.loging)

    api(libs.bundles.ktor)
}
