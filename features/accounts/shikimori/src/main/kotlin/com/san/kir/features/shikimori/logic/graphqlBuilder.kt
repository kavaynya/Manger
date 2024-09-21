package com.san.kir.features.shikimori.logic


internal object GraphQLQueries {
    private const val MANGA_FIELDS: String =
        "id name russian english score volumes chapters poster {originalUrl} genres {name russian} description"

    private const val RATE_FIELDS: String = "id score status chapters rewatches"

    fun userRateMangas(userId: Long, page: Int = 1): String {
        return graphqlBuilder(
            "userRates(targetType: Manga, limit:50, page: $page, userId: $userId)",
            listOf(
                RATE_FIELDS,
                "manga { id name russian english score volumes chapters poster {originalUrl} genres {name russian} description }",
            )
        )
    }

    fun queryCurrentUser(): String {
        return graphqlBuilder("currentUser", listOf("id", "nickname", "avatarUrl"))
    }

    fun searchManga(query: String, limit: Int = 50): String {
        return graphqlBuilder(
            "mangas(limit: $limit, search: \\\"$query\\\")",
            listOf(MANGA_FIELDS, "userRate { id score status chapters rewatches }"),
        )
    }

    fun mangaById(id: Long): String {
        return graphqlBuilder(
            "mangas(ids: \\\"$id\\\", limit: 1)",
            listOf(MANGA_FIELDS, "userRate { id score status chapters rewatches }"),
        )
    }
}

private fun graphqlBuilder(query: String, list: List<String>): String {
    return buildString {
        append("{\"query\":\"{")
        addEOL()
        append(query)
        append(" {")
        addEOL()
        append(list.joinToString(" "))
        addEOL()
        append("}")
        addEOL()
        append("}\"}")
    }
}

private fun StringBuilder.addEOL() {
    append("\\n  ")
}
