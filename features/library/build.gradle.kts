plugins {
    id("compose.library")
}

android {
    namespace = "com.san.kir.library"
}

dependencies {
    implementation(project(Modules.Core.compose))
    implementation(project(Modules.Core.utils))
    implementation(project(Modules.Core.support))
    implementation(project(Modules.Core.background))
    implementation(project(Modules.Data.db))
    implementation(project(Modules.Data.parsing))

    implementation(project(Modules.Features.chapters))
    implementation(project(Modules.Features.catalog))
    implementation(project(Modules.Features.viewer))
    implementation(project(Modules.Features.categories))
    implementation(project(Modules.Features.statistic))
    implementation(project(Modules.Features.storage))
    implementation(project(Modules.Features.settings))
    implementation(project(Modules.Features.schedule))
    implementation(project(Modules.Features.accounts))
}
