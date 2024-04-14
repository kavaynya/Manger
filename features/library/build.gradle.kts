plugins {
    id("compose.library")
}

android {
    namespace="com.san.kir.library"
}

dependencies {
    implementation(project(Modules.Core.compose))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.background))
    implementation(project(Modules.Data.db))
    implementation(project(Modules.Data.parsing))

    implementation(libs.timber)

    implementation(libs.lifecycle.livedata)
}
