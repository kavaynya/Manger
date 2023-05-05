plugins {
    id("base.library")
}

android {
    namespace = "com.san.kir.background"
}

dependencies {
    implementation(project(Modules.Data.models))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.support))
    implementation(project(Modules.Core.internet))
    implementation(project(Modules.Data.db))
    implementation(project(Modules.Data.parsing))

    implementation(libs.core)

    implementation(libs.bundles.coroutines)

    api(libs.work.runtime)
    implementation(libs.work.gcm)
    implementation(libs.work.multiprocess)
}
