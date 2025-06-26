package com.example.boltnew.data.model

import com.example.boltnew.data.model.advert.StrapiCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class StrapiCategoriesResponse(
    @kotlinx.serialization.SerialName("data")
    val data: List<StrapiCategory>
)

@Serializable
data class StrapiCategoryOption(
    val id: Int,
    val name: String,
    val slug: String
)

@Serializable
data class ProfileAdvertUpdateRequest(
    @SerialName("data")
    val data: ProfileAdvertUpdateData
)

@Serializable
data class ProfileAdvertUpdateData(
    @SerialName("adverts")
    val adverts: List<String>
)