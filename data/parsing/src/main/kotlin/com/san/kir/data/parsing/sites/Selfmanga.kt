package com.san.kir.data.parsing.sites

import com.san.kir.core.internet.ConnectManager

class Selfmanga(private val connectManager: ConnectManager) : ReadmangaTemplate(connectManager) {

    override val host: String
        get() = "https://$catalogName"

    override val name: String = "Self Manga"
    override val catalogName: String = "selfmanga.live"
    override val categories = listOf("Веб", "Сборник", "Ранобэ", "Журнал")
    override var volume = 0

    override val allCatalogName: List<String>
        get() = super.allCatalogName + "selfmanga.ru"

    override suspend fun init(): Selfmanga {
        connectManager
            .getDocument(host)
            .select(".rightContent .rightBlock h5")
            .filterNot { it.text() != "У нас сейчас" }
            .forEach { volume = it.parent()?.select("li b")?.first()?.text()?.toInt() ?: 0 }
        return this
    }
}
