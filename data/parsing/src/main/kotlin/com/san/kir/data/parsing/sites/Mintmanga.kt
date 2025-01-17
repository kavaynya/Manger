package com.san.kir.data.parsing.sites

import com.san.kir.core.internet.ConnectManager

class Mintmanga(connectManager: ConnectManager) : ReadmangaTemplate(connectManager) {

    override val host: String
        get() = "https://$catalogName"

    override val name: String = "Mint Manga"
    override val catalogName: String = "mintmanga.live"
    override var volume = 0

    override val allCatalogName: List<String>
        get() = super.allCatalogName + "mintmanga.com"

    override suspend fun init() = super.init() as Mintmanga
}
