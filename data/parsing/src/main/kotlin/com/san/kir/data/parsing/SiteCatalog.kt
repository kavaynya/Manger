package com.san.kir.data.parsing

import com.san.kir.core.internet.ConnectManager
import com.san.kir.data.models.catalog.SiteCatalogElement
import com.san.kir.data.models.main.Chapter
import com.san.kir.data.models.main.Manga
import io.ktor.util.StringValues
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

abstract class SiteCatalog {
    abstract val name: String
    abstract val catalogName: String

    open val host: String
        get() = "http://$catalogName"
    open val allCatalogName: List<String>
        get() = listOf(catalogName)

    open val headers: StringValues? = null
    open val hasPopulateSort = true
    open val servers = emptyList<String>()

    abstract val catalog: String

    abstract var volume: Int

    abstract suspend fun init(): SiteCatalog

    open suspend fun fullElement(element: SiteCatalogElement): SiteCatalogElement = element
    abstract fun catalog(): Flow<SiteCatalogElement>
    abstract suspend fun elementByUrl(url: String): SiteCatalogElement?
    abstract suspend fun chapters(manga: Manga): List<Chapter>
    abstract suspend fun pages(item: Chapter): List<String>
}

abstract class SiteCatalogClassic : SiteCatalog()

abstract class SiteCatalogAlternative : SiteCatalog()

fun SiteCatalog.getShortLink(fullLink: String): String {
    val foundedCatalogs = allCatalogName
        .filter { catalog -> fullLink.contains(catalog, true) }

    val shortLink: String
    if (foundedCatalogs.size == 1 || fullLink.contains(catalogName, true)) {
        shortLink = fullLink.split(foundedCatalogs.first()).last()
    } else {
        Timber.v("fullLink = $fullLink")
        Timber.v("foundedCatalogs = $foundedCatalogs")
        throw Throwable("Каталогов найдено больше одного или не найдено совсем")
    }

    return shortLink
}

data class LoginAvatar(val login: String, val avatar: String)

interface SiteConstants {
    val AUTH_URL: String
    val HOST_NAME: String
    val SITE_NAME: String
    suspend fun User(connectManager: ConnectManager): LoginAvatar? {
        return null
    }
}
