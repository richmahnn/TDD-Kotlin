package com.lubulwa.tddkotlin.example9

interface PingServerHttpEndpointSync {

    enum class EndpointResult {
        SUCCESS,
        GENERAL_ERROR,
        NETWORK_ERROR
    }

    fun pingServerSync() : EndpointResult

}