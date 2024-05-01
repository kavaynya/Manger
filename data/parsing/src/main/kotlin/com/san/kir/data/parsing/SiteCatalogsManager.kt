package com.san.kir.data.parsing

import android.content.Context
import com.san.kir.core.internet.ConnectManager
import com.san.kir.core.utils.DIR
import com.san.kir.core.utils.coroutines.withDefaultContext
import com.san.kir.core.utils.getFullPath
import com.san.kir.data.models.base.Chapter
import com.san.kir.data.models.base.Manga
import com.san.kir.data.models.base.SiteCatalogElement
import com.san.kir.data.parsing.sites.Acomics
import com.san.kir.data.parsing.sites.Allhentai
import com.san.kir.data.parsing.sites.Mangachan
import com.san.kir.data.parsing.sites.Mintmanga
import com.san.kir.data.parsing.sites.Readmanga
import com.san.kir.data.parsing.sites.Selfmanga
import com.san.kir.data.parsing.sites.Unicomics
import com.san.kir.data.parsing.sites.Yaoichan

class SiteCatalogsManager(
    context: Context,
    connectManager: ConnectManager,
) {

    init {
        Status.init(context)
        Translate.init(context)
    }

    val catalog by lazy {
        listOf(
            Mangachan(connectManager),
            Readmanga(connectManager),
            Mintmanga(connectManager),
            Selfmanga(connectManager),
            Allhentai(connectManager),
            Yaoichan(connectManager),
            Unicomics(connectManager),
            Acomics(connectManager),
            // ComX(connectManager),
        )
    }

    fun catalog(link: String): SiteCatalog {
        return catalog.first { siteCatalog ->
            siteCatalog.allCatalogName.any { link.contains(it) }
                    || siteCatalog.servers.any { link.contains(it) }
        }
    }

    fun catalogByName(catalogName: String): SiteCatalog {
        return catalog.firstOrNull { it.name == catalogName }
            ?: catalog.first { it.catalogName == catalogName }
    }

    suspend fun chapters(manga: Manga) = catalog(manga.host).chapters(manga)

    // Загрузка полной информации для элемента в каталоге
    suspend fun getFullElement(simpleElement: SiteCatalogElement) =
        withDefaultContext {
            catalog.first { it.allCatalogName.any { s -> s == simpleElement.catalogName } }
                .fullElement(simpleElement)
        }

    // Получение страниц
    suspend fun pages(item: Chapter) = catalog(item.link).pages(item)

    suspend fun elementByUrl(url: String): SiteCatalogElement? =
        withDefaultContext {
            var lUrl = url

            if (!lUrl.contains("http")) {
                lUrl = "http://$lUrl"
            }

            catalog(lUrl).elementByUrl(lUrl)
        }

    fun catalogName(siteName: String): String {
        val first = catalogByName(siteName)
        var catName = first.catalogName
        first.allCatalogName
            .firstOrNull { getFullPath(DIR.catalogName(catName)).exists() }
            ?.also { catName = it }

        return catName
    }
}
