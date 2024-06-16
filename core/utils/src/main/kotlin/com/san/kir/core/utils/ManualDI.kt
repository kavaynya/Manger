package com.san.kir.core.utils

import android.app.Application
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator
import com.san.kir.core.utils.navigation.NavComponent
import com.san.kir.core.utils.navigation.NavConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import kotlin.reflect.KClass

public object ManualDI {
    public const val TAG: String = "MANUAL DI"

    private var app: Application? = null
    private val navigationCreators: MutableMap<KClass<out NavConfig>, (NavConfig) -> NavComponent<*>> =
        hashMapOf()

    private val navigationAnimations: MutableMap<KClass<out NavConfig>, (NavConfig) -> StackAnimator> =
        hashMapOf()

    public fun init(application: Application) {
        app = application
    }

    public val application: Application
        get() = requireNotNull(app) { "call init before use" }

    @OptIn(ExperimentalSerializationApi::class)
    public val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }
    }

    public fun <T : NavConfig> addNavigationCreator(
        key: KClass<T>,
        creator: (T) -> NavComponent<T>
    ) {
        navigationCreators[key] = creator as (NavConfig) -> NavComponent<*>
    }

    internal fun navComponent(config: NavConfig): NavComponent<*>? {
        return navigationCreators[config::class]?.invoke(config)
    }

    public fun <T : NavConfig> addNavigationAnimation(
        key: KClass<T>,
        creator: (T) -> StackAnimator
    ) {
        navigationAnimations[key] = creator as (NavConfig) -> StackAnimator
    }

    internal fun navAnimation(config: NavConfig): StackAnimator? {
        return navigationAnimations[config::class]?.invoke(config)
    }

    public inline fun <reified Data> jsonToString(data: Data): String {
        return json.encodeToString(data)
    }

    public inline fun <reified Data> stringToJson(data: String): Data? {
        return runCatching {
            json.decodeFromString<Data>(data)
        }.onFailure {
            Timber.tag(TAG).e(it)
        }.getOrNull()
    }
}
