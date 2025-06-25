package com.example.boltnew.data.model.auth.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StrapiProfile(
    @SerialName("data")
    val `data`: Data = Data(),
    @SerialName("meta")
    val meta: Meta = Meta()
) {
    @Serializable
    data class Data(
        @SerialName("addresses")
        val addresses: List<Addresse> = listOf(),
        @SerialName("adverts")
        val adverts: List<Advert> = listOf(),
        @SerialName("avatar")
        val avatar: Avatar = Avatar(),
        @SerialName("createdAt")
        val createdAt: String = "",
        @SerialName("dob")
        val dob: String = "",
        @SerialName("documentId")
        val documentId: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("publishedAt")
        val publishedAt: String = "",
        @SerialName("updatedAt")
        val updatedAt: String = "",
        @SerialName("user")
        val user: User = User()
    ) {
        @Serializable
        data class Addresse(
            @SerialName("city")
            val city: String = "",
            @SerialName("country")
            val country: String = "",
            @SerialName("createdAt")
            val createdAt: String = "",
            @SerialName("documentId")
            val documentId: String = "",
            @SerialName("first_line_address")
            val firstLineAddress: String = "",
            @SerialName("first_name")
            val firstName: String = "",
            @SerialName("id")
            val id: Int = 0,
            @SerialName("last_name")
            val lastName: String = "",
            @SerialName("phone_number")
            val phoneNumber: String? = null,
            @SerialName("post_code")
            val postCode: String = "",
            @SerialName("publishedAt")
            val publishedAt: String = "",
            @SerialName("second_line_address")
            val secondLineAddress: String = "",
            @SerialName("updatedAt")
            val updatedAt: String = ""
        )

        @Serializable
        data class Advert(
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
            val slug: String? = null,
            @SerialName("title")
            val title: String = "",
            @SerialName("updatedAt")
            val updatedAt: String = ""
        )

        @Serializable
        data class Avatar(
            @SerialName("alternativeText")
            val alternativeText: String? = null,
            @SerialName("caption")
            val caption: String? = null,
            @SerialName("createdAt")
            val createdAt: String = "",
            @SerialName("documentId")
            val documentId: String = "",
            @SerialName("ext")
            val ext: String = "",
            @SerialName("formats")
            val formats: Formats = Formats(),
            @SerialName("hash")
            val hash: String = "",
            @SerialName("height")
            val height: Int = 0,
            @SerialName("id")
            val id: Int = 0,
            @SerialName("mime")
            val mime: String = "",
            @SerialName("name")
            val name: String = "",
            @SerialName("previewUrl")
            val previewUrl: String? = null,
            @SerialName("provider")
            val provider: String = "",
            @SerialName("provider_metadata")
            val providerMetadata: ProviderMetadata = ProviderMetadata(),
            @SerialName("publishedAt")
            val publishedAt: String = "",
            @SerialName("size")
            val size: Double = 0.0,
            @SerialName("updatedAt")
            val updatedAt: String = "",
            @SerialName("url")
            val url: String = "",
            @SerialName("width")
            val width: Int = 0
        ) {
            @Serializable
            data class Formats(
                @SerialName("large")
                val large: Large = Large(),
                @SerialName("medium")
                val medium: Medium = Medium(),
                @SerialName("small")
                val small: Small = Small(),
                @SerialName("thumbnail")
                val thumbnail: Thumbnail = Thumbnail()
            ) {
                @Serializable
                data class Large(
                    @SerialName("ext")
                    val ext: String = "",
                    @SerialName("hash")
                    val hash: String = "",
                    @SerialName("height")
                    val height: Int = 0,
                    @SerialName("mime")
                    val mime: String = "",
                    @SerialName("name")
                    val name: String = "",
                    @SerialName("path")
                    val path: String? = null,
                    @SerialName("provider_metadata")
                    val providerMetadata: ProviderMetadata = ProviderMetadata(),
                    @SerialName("size")
                    val size: Double = 0.0,
                    @SerialName("sizeInBytes")
                    val sizeInBytes: Int = 0,
                    @SerialName("url")
                    val url: String = "",
                    @SerialName("width")
                    val width: Int = 0
                ) {
                    @Serializable
                    data class ProviderMetadata(
                        @SerialName("public_id")
                        val publicId: String = "",
                        @SerialName("resource_type")
                        val resourceType: String = ""
                    )
                }

                @Serializable
                data class Medium(
                    @SerialName("ext")
                    val ext: String = "",
                    @SerialName("hash")
                    val hash: String = "",
                    @SerialName("height")
                    val height: Int = 0,
                    @SerialName("mime")
                    val mime: String = "",
                    @SerialName("name")
                    val name: String = "",
                    @SerialName("path")
                    val path: String? = null,
                    @SerialName("provider_metadata")
                    val providerMetadata: ProviderMetadata = ProviderMetadata(),
                    @SerialName("size")
                    val size: Double = 0.0,
                    @SerialName("sizeInBytes")
                    val sizeInBytes: Int = 0,
                    @SerialName("url")
                    val url: String = "",
                    @SerialName("width")
                    val width: Int = 0
                ) {
                    @Serializable
                    data class ProviderMetadata(
                        @SerialName("public_id")
                        val publicId: String = "",
                        @SerialName("resource_type")
                        val resourceType: String = ""
                    )
                }

                @Serializable
                data class Small(
                    @SerialName("ext")
                    val ext: String = "",
                    @SerialName("hash")
                    val hash: String = "",
                    @SerialName("height")
                    val height: Int = 0,
                    @SerialName("mime")
                    val mime: String = "",
                    @SerialName("name")
                    val name: String = "",
                    @SerialName("path")
                    val path: String? = null,
                    @SerialName("provider_metadata")
                    val providerMetadata: ProviderMetadata = ProviderMetadata(),
                    @SerialName("size")
                    val size: Double = 0.0,
                    @SerialName("sizeInBytes")
                    val sizeInBytes: Int = 0,
                    @SerialName("url")
                    val url: String = "",
                    @SerialName("width")
                    val width: Int = 0
                ) {
                    @Serializable
                    data class ProviderMetadata(
                        @SerialName("public_id")
                        val publicId: String = "",
                        @SerialName("resource_type")
                        val resourceType: String = ""
                    )
                }

                @Serializable
                data class Thumbnail(
                    @SerialName("ext")
                    val ext: String = "",
                    @SerialName("hash")
                    val hash: String = "",
                    @SerialName("height")
                    val height: Int = 0,
                    @SerialName("mime")
                    val mime: String = "",
                    @SerialName("name")
                    val name: String = "",
                    @SerialName("path")
                    val path: String? = null,
                    @SerialName("provider_metadata")
                    val providerMetadata: ProviderMetadata = ProviderMetadata(),
                    @SerialName("size")
                    val size: Double = 0.0,
                    @SerialName("sizeInBytes")
                    val sizeInBytes: Int = 0,
                    @SerialName("url")
                    val url: String = "",
                    @SerialName("width")
                    val width: Int = 0
                ) {
                    @Serializable
                    data class ProviderMetadata(
                        @SerialName("public_id")
                        val publicId: String = "",
                        @SerialName("resource_type")
                        val resourceType: String = ""
                    )
                }
            }

            @Serializable
            data class ProviderMetadata(
                @SerialName("public_id")
                val publicId: String = "",
                @SerialName("resource_type")
                val resourceType: String = ""
            )
        }

        @Serializable
        data class User(
            @SerialName("blocked")
            val blocked: Boolean = false,
            @SerialName("confirmed")
            val confirmed: Boolean = false,
            @SerialName("createdAt")
            val createdAt: String = "",
            @SerialName("documentId")
            val documentId: String = "",
            @SerialName("email")
            val email: String = "",
            @SerialName("id")
            val id: Int = 0,
            @SerialName("provider")
            val provider: String = "",
            @SerialName("publishedAt")
            val publishedAt: String = "",
            @SerialName("updatedAt")
            val updatedAt: String = "",
            @SerialName("username")
            val username: String = ""
        )
    }

    @Serializable
    class Meta
}