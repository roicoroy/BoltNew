package com.example.boltnew.data.mapper

import com.example.boltnew.data.Product
import com.example.boltnew.data.database.ProductEntity

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        category = category,
        rating = rating,
        reviewCount = reviewCount,
        inStock = inStock
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        category = category,
        rating = rating,
        reviewCount = reviewCount,
        inStock = inStock
    )
}

fun List<ProductEntity>.toDomain(): List<Product> {
    return map { it.toDomain() }
}

fun List<Product>.toEntity(): List<ProductEntity> {
    return map { it.toEntity() }
}