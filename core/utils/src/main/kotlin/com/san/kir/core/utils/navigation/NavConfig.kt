package com.san.kir.core.utils.navigation

import com.arkivanov.essenty.statekeeper.ExperimentalStateKeeperApi
import com.arkivanov.essenty.statekeeper.polymorphicSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

@Serializable
public abstract class NavConfig {
    @OptIn(ExperimentalSerializationApi::class, ExperimentalStateKeeperApi::class)
    internal companion object {
        fun serializer(serializersModule: SerializersModule) =
            object : KSerializer<NavConfig> by polymorphicSerializer<NavConfig>(serializersModule) {}
    }
}
