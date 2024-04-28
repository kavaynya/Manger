package com.san.kir.data.parsing.sites

import com.san.kir.core.internet.ConnectManager
import com.san.kir.data.parsing.LoginAvatar
import com.san.kir.data.parsing.SiteConstants
import org.jsoup.nodes.Document

class Allhentai(connectManager: ConnectManager) : ReadmangaTemplate(connectManager) {
    override val name = SITE_NAME
    override val catalogName = HOST_NAME
    override var volume = 0

    override val allCatalogName: List<String>
        get() = super.allCatalogName + "allhentai.ru" + "23.allhen.online" + "22.allhen.online"

    override val categories = listOf(
        "3D",
        "Анимация",
        "Без текста",
        "Порно комикс",
        "Порно манхва"
    )

    override fun checkAuthorization(document: Document): Boolean {
        val text = document.select(".container .auth-page .alert").text()
        return "нужно авторизоваться!" in text
    }

    companion object : SiteConstants {
        override val SITE_NAME = "All Hentai"
        override val HOST_NAME = "2023.allhen.online"
        override val AUTH_URL = "$HOST_NAME/internal/auth"

        override suspend fun User(connectManager: ConnectManager): LoginAvatar? {
            val document = connectManager.getDocument(HOST_NAME)
            val doc = document.select(".account-menu")
            val name = doc.select("#accountMenu span.strong").first()?.text() ?: return null
            return LoginAvatar(
                login = name,
                avatar = document.select(".user-profile-settings-link img").attr("src")
            )
        }
    }
}
