package com.lubulwa.tddkotlin.example7

import com.lubulwa.tddkotlin.example7.eventbus.EventBusPoster
import com.lubulwa.tddkotlin.example7.eventbus.UserDetailsChangedEvent
import com.lubulwa.tddkotlin.example7.networking.NetworkErrorException
import com.lubulwa.tddkotlin.example7.networking.UpdateUsernameHttpEndpointSync
import com.lubulwa.tddkotlin.example7.networking.UpdateUsernameHttpEndpointSync.EndpointResult
import com.lubulwa.tddkotlin.example7.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus
import com.lubulwa.tddkotlin.example7.users.User
import com.lubulwa.tddkotlin.example7.users.UsersCache

class UpdateUsernameUseCaseSync(
    private val mUpdateUsernameHttpEndpointSync: UpdateUsernameHttpEndpointSync,
    private val mUsersCache: UsersCache,
    private val mEventBusPoster: EventBusPoster
) {

    enum class UseCaseResult {
        SUCCESS,
        FAILURE,
        NETWORK_ERROR
    }

    fun updateUsernameSync(userId: String, username: String): UseCaseResult {
        var endpointResult: EndpointResult? = null
        try {
            endpointResult = mUpdateUsernameHttpEndpointSync.updateUsername(userId, username)
        } catch (e: NetworkErrorException) {
            // the bug here is "swallowed" exception instead of return
            return UseCaseResult.NETWORK_ERROR
        }

        return if (isSuccessfulEndpointResult(endpointResult)) {
            val user = User(endpointResult.userId, endpointResult.getUsername())
            mEventBusPoster.postEvent(UserDetailsChangedEvent(User(userId, username)))
            mUsersCache.cacheUser(user)
            UseCaseResult.SUCCESS
        } else {
            UseCaseResult.FAILURE
        }
    }

    private fun isSuccessfulEndpointResult(endpointResult: EndpointResult): Boolean {
        // the bug here is the wrong definition of successful response
        return endpointResult.status == EndpointResultStatus.SUCCESS
        // return endpointResult.status == EndpointResultStatus.SUCCESS || endpointResult.status == EndpointResultStatus.GENERAL_ERROR
    }
}
