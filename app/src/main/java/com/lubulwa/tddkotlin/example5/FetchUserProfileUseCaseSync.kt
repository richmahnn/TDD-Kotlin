package com.lubulwa.tddkotlin.example5

import com.lubulwa.tddkotlin.example4.networking.NetworkErrorException
import com.lubulwa.tddkotlin.example5.networking.UserProfileHttpEndpointSync
import com.lubulwa.tddkotlin.example5.networking.UserProfileHttpEndpointSync.EndpointResult
import com.lubulwa.tddkotlin.example5.users.User
import com.lubulwa.tddkotlin.example5.users.UsersCache

class FetchUserProfileUseCaseSync(
    private val mUserProfileHttpEndpointSync: UserProfileHttpEndpointSync,
    private val mUsersCache: UsersCache
) {

    enum class UseCaseResult {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR
    }

    fun fetchUserProfileSync(userId: String): UseCaseResult {
        val endpointResult: EndpointResult
        try {
            // the bug here is that userId is not passed to endpoint
            endpointResult = mUserProfileHttpEndpointSync.getUserProfile(userId)
            // the bug here is that I don't check for successful result and it's also a duplication
            // of the call later in this method
//            when(endpointResult.status) {
//                UseCaseResult.SUCCESS ->
//            }
            mUsersCache.cacheUser(
                User(userId, endpointResult.getFullName(), endpointResult.getImageUrl())
            )
        } catch (e: NetworkErrorException) {
            return UseCaseResult.NETWORK_ERROR
        }

        if (isSuccessfulEndpointResult(endpointResult)) {
            mUsersCache.cacheUser(
                User(userId, endpointResult.getFullName(), endpointResult.getImageUrl())
            )

            return UseCaseResult.SUCCESS
        }

        // the bug here is that I return wrong result in case of an unsuccessful server response
        return UseCaseResult.FAILURE
    }

    private fun isSuccessfulEndpointResult(endpointResult: EndpointResult): Boolean {
        return endpointResult.getStatus() === UserProfileHttpEndpointSync.EndpointResultStatus.SUCCESS
    }
}
