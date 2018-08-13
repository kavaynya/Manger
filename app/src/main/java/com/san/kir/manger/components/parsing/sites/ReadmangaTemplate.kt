package com.san.kir.manger.components.parsing.sites

import com.san.kir.manger.components.main.Main
import com.san.kir.manger.components.parsing.ManageSites
import com.san.kir.manger.components.parsing.SiteCatalog
import com.san.kir.manger.room.models.Chapter
import com.san.kir.manger.room.models.DownloadItem
import com.san.kir.manger.room.models.Manga
import com.san.kir.manger.room.models.SiteCatalogElement
import com.san.kir.manger.utils.createDirs
import com.san.kir.manger.utils.getFullPath
import kotlinx.coroutines.experimental.channels.produce
import org.json.JSONArray
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.regex.Pattern
import kotlin.coroutines.experimental.CoroutineContext

open class ReadmangaTemplate : SiteCatalog {
    override var isInit = false
    override val id = 1
    override val name = "Read Manga"
    override val catalogName = "readmanga.me"
    override val host
        get() = "http://$catalogName"
    override val siteCatalog
        get() = "$host/list?sortType=created"
    override var volume = 0
    override var oldVolume = 0
    open val categories = listOf("Ёнкома",
                                 "Комикс западный",
                                 "Манхва",
                                 "Маньхуа",
                                 "В цвете",
                                 "Веб",
                                 "Сборник")

    override fun init(): ReadmangaTemplate {
        if (!isInit) {
            oldVolume = Main.db.siteDao.loadSite(name)?.volume ?: 0
            val doc = ManageSites.getDocument(host)
            doc.select(".rightContent h5").forEach {
                if (it.text() == "У нас сейчас")
                    volume = it.parent().select("li b").first().text().toInt()
            }
            isInit = true
        }
        return this
    }

    ///
    override fun getFullElement(element: SiteCatalogElement): SiteCatalogElement {

        val doc = ManageSites.getDocument(element.link).select("div.leftContent")

        element.type = "Манга"
        doc.select(".flex-row .subject-meta .elem_tag").forEach {
            if (categories.contains(it.select("a").text()))
                element.type = it.select("a").text()
        }

        element.authors = emptyList()
        val authorsTemp = doc.select(".flex-row .elementList .elem_author")
        element.authors = authorsTemp.map { it.select(".person-link").text() }

        val volume = doc.select(".chapters-link tr").size - 1
        element.volume = if (volume < 0) 0 else volume

        element.genres.clear()
        doc.select("span.elem_genre").forEach { element.genres.add(it.select("a.element-link").text()) }

        element.about = doc.select("meta[itemprop=description]").attr("content")

        element.isFull = true

        return element
    }

    private fun simpleParseElement(elem: Element): SiteCatalogElement {
        val element = SiteCatalogElement()

        element.host = host
        element.catalogName = catalogName
        element.siteId = id

        element.name = elem.select(".img a").select("img").attr("title")

        element.shotLink = elem.select(".img a").attr("href")
        element.link = host + element.shotLink

        element.statusEdition = "Выпуск продолжается"
        if (elem.select("span.mangaCompleted").text().isNotEmpty())
            element.statusEdition = "Выпуск завершен"
        else if (elem.select("span.mangaSingle").text().isNotEmpty() and (element.volume > 0))
            element.statusEdition = "Сингл"

        element.statusTranslate = "Перевод продолжается"
        if (elem.select("span.mangaTranslationCompleted").text().isNotEmpty()) {
            element.statusTranslate = "Перевод завершен"
            element.statusEdition = "Выпуск завершен"
        }

        element.logo = elem.select(".img a").select("img").attr("data-original")

        element.dateId = elem.select(".chapters span.bookmark-menu").attr("data-id").toInt()

        try {
            element.populate = elem.select(".desc .tile-info p.small").first().ownText().toInt()
        } catch (ex: Throwable) {
            element.populate = 0
        }

        element.type = "Манга"

        element.authors = elem.select(".desc .tile-info .person-link")
                .map { it.select(".person-link").text() }

        elem.select(".desc .tile-info a.element-link").forEach { element.genres.add(it.text()) }

        return element
    }

    override fun getCatalog(context: CoroutineContext) = produce(context) {
        var docLocal: Document = ManageSites.getDocument(siteCatalog)

        fun isGetNext(): Boolean {
            val next = docLocal.select(".pagination > a.nextLink").attr("href")
            if (next.isNotEmpty())
                docLocal = ManageSites.getDocument(host + next)
            return next.isNotEmpty()
        }

        do {
            docLocal.select("div.tile").forEach { element ->
                send(simpleParseElement(element))
            }
        } while (isGetNext())
        close()
    }
    ///


    override fun chapters(manga: Manga) =
            ManageSites.getDocument(manga.site)
                    .select("div.leftContent .chapters-link")
                    .select("tr")
                    .filter { it.select("a").text().isNotEmpty() }
                    .map {
                        var name = it.select("a").text()
                        val pat = Pattern.compile("v.+").matcher(name)
                        if (pat.find())
                            name = pat.group()
                        Chapter(manga = manga.unic,
                                name = name,
                                date = it.select("td").last().text(),
                                site = host + it.select("a").attr("href"),
                                path = "${manga.path}/$name")
                    }



    override fun pages(item: DownloadItem): List<String> {
        var list = listOf<String>()
        // Создаю папку/папки по указанному пути
        createDirs(getFullPath(item.path))
        val doc = ManageSites.getDocument(item.link + "?mature=1")
        // с помощью регулярных выражений ищу нужные данные
        val pat = Pattern.compile("rm_h.init.+").matcher(doc.body().html())
        // если данные найдены то продолжаю
        if (pat.find()) {
            // избавляюсь от ненужного и разделяю строку в список и отправляю
            val data = pat.group()
                    .removeSuffix(", 0, false);")
                    .removePrefix("rm_h.init( ")

            val json = JSONArray(data)

            repeat(json.length()) { index ->
                val jsonArray = json.getJSONArray(index)
                val url = jsonArray.getString(1) +
                        jsonArray.getString(0) +
                        jsonArray.getString(2)
                list += url
            }
        }
        return list
    }
}
