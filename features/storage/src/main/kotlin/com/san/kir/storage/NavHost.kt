package com.san.kir.storage

import NavEntry
import androidx.compose.runtime.remember
import com.san.kir.core.compose.animation.SharedParams
import com.san.kir.core.compose.animation.horizontalSlide
import com.san.kir.core.compose.animation.shapeAnimator
import com.san.kir.core.utils.navigation.NavConfig
import com.san.kir.core.utils.navigation.navAnimation
import com.san.kir.core.utils.navigation.navCreator
import com.san.kir.storage.ui.storage.StorageScreen
import com.san.kir.storage.ui.storages.StoragesScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

public val storageSerializersModule: SerializersModule = AddNavigationCreators.serializerModule()

@NavEntry
@Serializable
public data object Storages : NavConfig() {
    internal val creator = navCreator<Storages> {
        StoragesScreen(
            navigateUp = backPressed(),
            navigateToItem = add(::Storage)
        )
    }

    internal val animation = navAnimation<Storages> { horizontalSlide() }
}

@NavEntry
@Serializable
public class Storage(
    internal val mangaId: Long,
    internal val params: SharedParams,
    internal val hasUpdate: Boolean = false,
) : NavConfig() {
    internal companion object {
        internal val creator = navCreator<Storage> { config ->
            StorageScreen(
                navigateUp = backPressed(),
                mangaId = remember { config.mangaId },
                hasUpdate = remember { config.hasUpdate },
            )
        }

        internal val animation = navAnimation<Storage> { shapeAnimator(it.params) }
    }
}
