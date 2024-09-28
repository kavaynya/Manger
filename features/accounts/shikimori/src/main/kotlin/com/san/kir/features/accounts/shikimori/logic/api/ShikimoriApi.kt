package com.san.kir.features.accounts.shikimori.logic.api

import com.san.kir.features.accounts.shikimori.logic.models.ShikimoriStatus
import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Resource("/api")
internal class ShikimoriApi {

    @Serializable
    @Resource("users")
    class Users(val parent: ShikimoriApi = ShikimoriApi()) {

        @Serializable
        @Resource("whoami")
        class Whoami(val parent: Users = Users())
    }

    @Serializable
    @Resource("mangas")
    class Mangas(
        val parent: ShikimoriApi = ShikimoriApi(),
        val search: String = "",
        val limit: Int = 50,
        val order: String = "name",
        val ids: String = "",
    ) {
        @Serializable
        @Resource("{id}")
        class Id(val parent: Mangas = Mangas(), val id: Long)
    }

    @Serializable
    @Resource("v2")
    class V2(val parent: ShikimoriApi = ShikimoriApi()) {

        @Serializable
        @Resource("user_rates")
        class UserRates(val parent: V2 = V2()) {

            @Serializable
            @Resource("{id}")
            class Id(val parent: UserRates = UserRates(), val id: Long)
        }
    }

    @Serializable
    @Resource("graphql")
    class Graphql(val parent: ShikimoriApi = ShikimoriApi())

    class Data {

        @Serializable
        class UpdateRate(@SerialName("user_rate") val userRate: Rate) {

            @Serializable
            class Rate(
                @SerialName("status") val status: ShikimoriStatus,
                @SerialName("chapters") val read: Int,
                @SerialName("rewatches") val rewatches: Int,
                @SerialName("score") val score: Int
            )
        }

        @Serializable
        class CreateRate(@SerialName("user_rate") val userRate: Rate) {

            @Serializable
            class Rate(
                @SerialName("user_id") val userId: Long,
                @SerialName("target_id") val targetId: Long,
                @SerialName("target_type") val targetType: String,
            )
        }

        companion object {
            fun UpdateRate(
                status: ShikimoriStatus,
                read: Int,
                rewatches: Int,
                score: Int
            ): UpdateRate {
                return UpdateRate(UpdateRate.Rate(status, read, rewatches, score))
            }

            fun CreateRate(accountId: Long, idInSite: Long): CreateRate {
                return CreateRate(CreateRate.Rate(accountId, idInSite, "Manga"))
            }
        }
    }
}

