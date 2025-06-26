package com.example.boltnew.data.repository.address

import com.example.boltnew.data.model.auth.profile.Address

interface AddressRepository {
    suspend fun createAddress(address: Address, profileDocumentId: String): Result<Address>
    suspend fun updateAddress(address: Address, profileDocumentId: String): Result<Address>
    suspend fun deleteAddress(addressDocumentId: String, profileDocumentId: String): Result<Boolean>
}