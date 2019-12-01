package com.lubulwa.tddkotlin.example8

import com.lubulwa.tddkotlin.example8.networking.AddToCartHttpEndpointSync
import com.lubulwa.tddkotlin.example8.networking.AddToCartHttpEndpointSync.EndpointResult
import com.lubulwa.tddkotlin.example8.networking.AddToCartHttpEndpointSync.EndpointResult.*
import com.lubulwa.tddkotlin.example8.networking.CartItemScheme
import com.lubulwa.tddkotlin.example8.networking.NetworkErrorException

class AddToCartUseCaseSync(val addToCartHttpEndpointSync: AddToCartHttpEndpointSync) {

    enum class UseCaseResult {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR
    }

    fun addToCartSync(offerid: String, amount: Int) : UseCaseResult {
        val result: EndpointResult
        try {
            result = addToCartHttpEndpointSync.addToCartSync(CartItemScheme(offerid, amount))
        } catch (e: NetworkErrorException) {
            return UseCaseResult.NETWORK_ERROR
        }

        return when(result) {
            SUCCESS -> UseCaseResult.SUCCESS
            AUTH_ERROR, GENERAL_ERROR -> UseCaseResult.FAILURE
        }
    }

}