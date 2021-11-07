object Dependencies {
    object Kotlin {
        const val STDLIB =
            "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin.STDLIB}"
        const val COROUTINES_CORE =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.COROUTINES}"
        const val COROUTINES_ANDROID =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.COROUTINES}"
    }

    object AndroidX {

        object Room {
            const val ROOM_RUNTIME =
                "androidx.room:room-runtime:${Versions.AndroidX.ROOM}"
            const val ROOM_COMPILER =
                "androidx.room:room-compiler:${Versions.AndroidX.ROOM}"
            const val ROOM_KTX =
                "androidx.room:room-ktx:${Versions.AndroidX.ROOM}"
            const val ROOM_PAGING =
                "androidx.room:room-paging:${Versions.AndroidX.ROOM}"
        }

        object Lifecycle {
            const val LIFECYCLE_VIEWMODEL =
                "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.LIFECYCLE}"
            const val LIFECYCLE_RUNTIME =
                "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.LIFECYCLE}"
            const val LIFECYCLE_LIVEDATA =
                "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.AndroidX.LIFECYCLE}"
            const val LIFECYCLE_COMMON =
                "androidx.lifecycle:lifecycle-common-java8:${Versions.AndroidX.LIFECYCLE}"
            const val LIFECYCLE_PROCESS =
                "androidx.lifecycle:lifecycle-process:${Versions.AndroidX.LIFECYCLE}"
            const val LIFECYCLE_SERVICE =
                "androidx.lifecycle:lifecycle-service:${Versions.AndroidX.LIFECYCLE}"
        }

        object WorkManager {
            const val WORK_RUNTIME =
                "androidx.work:work-runtime-ktx:${Versions.AndroidX.WORKMANAGER}"
            const val WORK_GCM =
                "androidx.work:work-gcm:${Versions.AndroidX.WORKMANAGER}"
            const val WORK_MULTIPROCESS =
                "androidx.work:work-multiprocess:${Versions.AndroidX.WORKMANAGER}"
        }

        object Datastore {
            const val DATASTORE =
                "androidx.datastore:datastore:${Versions.AndroidX.DATASTORE}"
        }

        object Hilt {
            const val HILT_COMPILER =
                "androidx.hilt:hilt-compiler:${Versions.AndroidX.HILT}"
            const val HILT_WORK =
                "androidx.hilt:hilt-work:${Versions.AndroidX.HILT}"
        }

        const val CORE =
            "androidx.core:core-ktx:${Versions.AndroidX.CORE}"
        const val SPLASH =
            "androidx.core:core-splashscreen:${Versions.AndroidX.SPLASH}"
        const val APPCOMPAT =
            "androidx.appcompat:appcompat:${Versions.AndroidX.APPCOMPAT}"
        const val COLLECTION =
            "androidx.collection:collection-ktx:${Versions.AndroidX.COLLECTION}"
        const val PREFERENCE =
            "androidx.preference:preference-ktx:${Versions.AndroidX.PREFERENCE}"
        const val VECTORDRAWABLE =
            "androidx.vectordrawable:vectordrawable:${Versions.AndroidX.VECTORDRAWABLE}"
        const val CONSTRAINTLAYOUT =
            "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.CONSTRAINTLAYOUT}"
    }

    object Google {
        object Accompanist {
            const val FLOWLAYOUT =
                "com.google.accompanist:accompanist-flowlayout:${Versions.Google.ACCOMPANIST}"
            const val PAGER =
                "com.google.accompanist:accompanist-pager:${Versions.Google.ACCOMPANIST}"
            const val PAGER_INDICATORS =
                "com.google.accompanist:accompanist-pager-indicators:${Versions.Google.ACCOMPANIST}"
            const val PERMISSIONS =
                "com.google.accompanist:accompanist-permissions:${Versions.Google.ACCOMPANIST}"
            const val SYSTEMUICONTROLLER =
                "com.google.accompanist:accompanist-systemuicontroller:${Versions.Google.ACCOMPANIST}"
            const val INSETS =
                "com.google.accompanist:accompanist-insets:${Versions.Google.ACCOMPANIST}"
            const val INSETS_UI =
                "com.google.accompanist:accompanist-insets-ui:${Versions.Google.ACCOMPANIST}"
            const val NAVIGATION_ANIMATION =
                "com.google.accompanist:accompanist-navigation-animation:${Versions.Google.ACCOMPANIST}"
        }

        object Hilt {
            const val HILT_ANDROID =
                "com.google.dagger:hilt-android:${Versions.Google.HILT}"
            const val HILT_COMPILER =
                "com.google.dagger:hilt-compiler:${Versions.Google.HILT}"
        }

        const val PROTOBUF_JAVALITE =
            "com.google.protobuf:protobuf-javalite:${Versions.Google.PROTOBUF_JAVALITE}"
        const val PROTOBUF_PROTOC =
            "com.google.protobuf:protoc:${Versions.Google.PROTOBUF_PROTOC}"
        const val MATERIAL =
            "com.google.android.material:material:${Versions.Google.MATERIAL}"
        const val PLAY_SERVICES_GCM =
            "com.google.android.gms:play-services-gcm:${Versions.Google.PLAY_SERVICES_GCM}"
    }

    object ForInternet {
        const val JSOUP =
            "org.jsoup:jsoup:${Versions.ForInternet.JSOUP}"
        const val GSON =
            "com.google.code.gson:gson:${Versions.ForInternet.GSON}"
        const val OKIO =
            "com.squareup.okio:okio:${Versions.ForInternet.OKIO}"
        const val RETROFIT =
            "com.squareup.retrofit2:retrofit:${Versions.ForInternet.RETROFIT}"
    const val OKHTTP =
            "com.squareup.okhttp3:okhttp:${Versions.ForInternet.OKHTTP}"
    }

    object Compose {
        const val UI =
            "androidx.compose.ui:ui:${Versions.Compose.COMPOSE}"
        const val UI_TOOLING =
            "androidx.compose.ui:ui-tooling:${Versions.Compose.COMPOSE}"
        const val UI_TOOLING_PREVIEW =
            "androidx.compose.ui:ui-tooling-preview:${Versions.Compose.COMPOSE}"
        const val RUNTIME =
            "androidx.compose.runtime:runtime:${Versions.Compose.COMPOSE}"
        const val COMPILER =
            "androidx.compose.compiler:compiler:${Versions.Compose.COMPOSE}"
        const val ANIMATION =
            "androidx.compose.animation:animation:${Versions.Compose.COMPOSE}"
        const val FOUNDATION =
            "androidx.compose.foundation:foundation:${Versions.Compose.COMPOSE}"
        const val FOUNDATION_LAYOUT =
            "androidx.compose.foundation:foundation-layout:${Versions.Compose.COMPOSE}"
        const val MATERIAL =
            "androidx.compose.material:material:${Versions.Compose.COMPOSE}"
        const val MATERIAL_ICONS_CORE =
            "androidx.compose.material:material-icons-core:${Versions.Compose.COMPOSE}"
        const val MATERIAL_ICONS_EXTENDED =
            "androidx.compose.material:material-icons-extended:${Versions.Compose.COMPOSE}"

        const val HILT_NAVIGATION =
            "androidx.hilt:hilt-navigation-compose:${Versions.Compose.HILT_NAVIGATION_COMPOSE}"
        const val PAGING_COMPOSE =
            "androidx.paging:paging-compose:${Versions.Compose.PAGING}"
    }

    object Hyperion {
        const val CORE =
            "com.willowtreeapps.hyperion:hyperion-core:${Versions.HYPERION}"
        const val CRASH =
            "com.willowtreeapps.hyperion:hyperion-crash:${Versions.HYPERION}"
    }

    object Test {
        const val JUNIT =
            "junit:junit:${Versions.Test.JUNIT}"
        const val TEST_CORE =
            "androidx.test:core:${Versions.Test.TEST_CORE}"
        const val TEST_RULES =
            "androidx.test:rules:${Versions.Test.TEST_RULES}"
        const val TEST_JUNIT =
            "androidx.test.ext:junit:${Versions.Test.TEST_JUNIT}"
        const val TEST_RUNNER =
            "androidx.test:runner:${Versions.Test.TEST_RUNNER}"
        const val TRUTH =
            "com.google.truth:truth:${Versions.Test.TRUTH}"
        const val BENCHMARK_JUNIT =
            "androidx.benchmark:benchmark-junit4:${Versions.Test.BENCHMARK_JUNIT}"
        const val COMPOSE_JUNIT =
            "androidx.compose.ui:ui-test-junit4:${Versions.Compose.COMPOSE}"
        const val COMPOSE_MANIFEST =
            "androidx.compose.ui:ui-test-manifest:${Versions.Compose.COMPOSE}"
        const val KAKAOCUP =
            "io.github.kakaocup:compose:${Versions.Test.KAKAOCUP}"
        const val ESPRESSO =
            "androidx.test.espresso:espresso-core:${Versions.Test.ESPRESSO}"
        const val NAVIGATION =
            "androidx.navigation:navigation-testing:${Versions.AndroidX.NAVIGATION}"
    }
}

