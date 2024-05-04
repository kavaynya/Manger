plugins {
    id("base.library")
}

android {
    namespace = "com.san.kir.data.parsing"
}

dependencies {
    implementation(project(Modules.Core.internet))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Data.models))

    implementation(libs.coroutines.core)
}
