package com.lubulwa.tddkotlin.example4

import com.lubulwa.tddkotlin.example4.authtoken.AuthTokenCache
import com.lubulwa.tddkotlin.example4.eventbus.EventBusPoster
import com.lubulwa.tddkotlin.example4.eventbus.LoggedInEvent
import com.lubulwa.tddkotlin.example4.networking.LoginHttpEndpointSync
import com.lubulwa.tddkotlin.example4.networking.NetworkErrorException

class LoginUseCaseSync(
    private val mLoginHttpEndpointSync: LoginHttpEndpointSync,
    private val mAuthTokenCache: AuthTokenCache,
    private val mEventBusPoster: EventBusPoster
) {

    enum class UseCaseResult {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR
    }

    fun loginSync(username: String, password: String): UseCaseResult {
        val endpointEndpointResult: LoginHttpEndpointSync.EndpointResult
        try {
            endpointEndpointResult = mLoginHttpEndpointSync.loginSync(username, password)
        } catch (e: NetworkErrorException) {
            return UseCaseResult.NETWORK_ERROR
        }


        if (isSuccessfulEndpointResult(endpointEndpointResult)) {
            mAuthTokenCache.cacheAuthToken(endpointEndpointResult.authToken)
            mEventBusPoster.postEvent(LoggedInEvent())
            return UseCaseResult.SUCCESS
        } else {
            return UseCaseResult.FAILURE
        }
    }

    private fun isSuccessfulEndpointResult(endpointResult: LoginHttpEndpointSync.EndpointResult): Boolean {
        return endpointResult.status == LoginHttpEndpointSync.EndpointResultStatus.SUCCESS
    }
}
