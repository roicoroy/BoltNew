package com.example.boltnew.data.model.advert


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvertCreateResponse(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("meta")
    val meta: Meta = Meta()
) {
    @Serializable
    data class Data(
        @SerialName("createdAt")
        val createdAt: String = "",
        @SerialName("description")
        val description: String = "",
        @SerialName("documentId")
        val documentId: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("publishedAt")
        val publishedAt: String = "",
        @SerialName("slug")
        val slug: String = "",
        @SerialName("title")
        val title: String = "",
        @SerialName("updatedAt")
        val updatedAt: String = ""
    )

    @Serializable
    class Meta
}