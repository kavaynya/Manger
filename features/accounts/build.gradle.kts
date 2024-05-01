plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.accounts"
}

dependencies {
    implementation(project(Modules.Core.compose))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Features.shikimori))
    implementation(project(Modules.Features.Catalogs.allhen))
}
