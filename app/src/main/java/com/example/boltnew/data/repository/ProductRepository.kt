package com.example.boltnew.data.repository

import com.example.boltnew.data.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    fun getProductsByCategory(category: String): Flow<List<Product>>
    fun getInStockProducts(): Flow<List<Product>>
    suspend fun insertProduct(product: Product)
    suspend fun insertProducts(products: List<Product>)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun initializeData()
}