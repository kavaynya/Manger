package com.san.kir.data.parsing.sites

import com.san.kir.core.internet.ConnectManager
import com.san.kir.data.parsing.LoginAvatar
import com.san.kir.data.parsing.SiteConstants

public object MintmangaConstants : SiteConstants {
    override val SITE_NAME: String = "Mint Manga"
    override val HOST_NAME: String = "23.mintmanga.one"
    override val AUTH_URL: String = "$HOST_NAME/internal/auth"

    override suspend fun User(connectManager: ConnectManager): LoginAvatar? {
        val document = connectManager.getDocument(HOST_NAME)
        val name = document
            .select(".account-menu")
            .select("#accountMenu span.strong")
            .firstOrNull()?.text()
        if (name == null) return null
        val avatar = document.select(".user-profile-settings-link img").attr("src")
        return LoginAvatar(name, avatar)
    }
}

internal class Mintmanga(connectManager: ConnectManager) : ReadmangaTemplate(connectManager) {

    override val host: String
        get() = "https://$catalogName"

    override val name: String = MintmangaConstants.SITE_NAME
    override val catalogName: String = MintmangaConstants.HOST_NAME
    override var volume = 0

    override val allCatalogName: List<String>
        get() = super.allCatalogName + "mintmanga.com" + "mintmanga.live" + "23.mintmanga.live"

    override suspend fun init() = super.init() as Mintmanga
}
