package com.example.boltnew.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// Data classes for address operations
@Serializable
data class StrapiAddressSingleResponse(
    @SerialName("data")
    val data: StrapiAddressData,
    @SerialName("meta")
    val meta: StrapiMeta = StrapiMeta()
)

@Serializable
data class StrapiAddressData(
    @SerialName("id")
    val id: Int,
    @SerialName("documentId")
    val documentId: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("first_line_address")
    val firstLineAddress: String,
    @SerialName("second_line_address")
    val secondLineAddress: String? = null,
    @SerialName("post_code")
    val postCode: String,
    @SerialName("city")
    val city: String,
    @SerialName("country")
    val country: String,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("publishedAt")
    val publishedAt: String
)

@Serializable
data class StrapiAddressCreateRequest(
    @SerialName("data")
    val data: StrapiAddressCreateData
)

@Serializable
data class StrapiAddressCreateData(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("first_line_address")
    val firstLineAddress: String,
    @SerialName("second_line_address")
    val secondLineAddress: String? = null,
    @SerialName("post_code")
    val postCode: String,
    @SerialName("city")
    val city: String,
    @SerialName("country")
    val country: String,
    @SerialName("phone_number")
    val phoneNumber: String? = null
)

@Serializable
data class StrapiAddressUpdateRequest(
    @SerialName("data")
    val data: StrapiAddressUpdateData
)

@Serializable
data class StrapiAddressUpdateData(
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("first_line_address")
    val firstLineAddress: String? = null,
    @SerialName("second_line_address")
    val secondLineAddress: String? = null,
    @SerialName("post_code")
    val postCode: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("country")
    val country: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null
)

@Serializable
data class ProfileAddressUpdateRequest(
    @SerialName("data")
    val data: ProfileAddressUpdateData
)

@Serializable
data class ProfileAddressUpdateData(
    @SerialName("addresses")
    val addresses: List<String>
)

@Serializable
data class StrapiMeta(
    @SerialName("pagination")
    val pagination: StrapiPagination? = null
)

@Serializable
data class StrapiPagination(
    @SerialName("page")
    val page: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 25,
    @SerialName("pageCount")
    val pageCount: Int = 1,
    @SerialName("total")
    val total: Int = 0
)