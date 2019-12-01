package com.lubulwa.tddkotlin.example5

import com.lubulwa.tddkotlin.example4.networking.NetworkErrorException
import com.lubulwa.tddkotlin.example5.networking.UserProfileHttpEndpointSync
import com.lubulwa.tddkotlin.example5.users.User
import com.lubulwa.tddkotlin.example5.users.UsersCache
import com.nhaarman.mockitokotlin2.isNull
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class FetchUserProfileUseCaseSyncTest {

    private val USERID = "1234"
    private val FULLNAME = "John Doe"
    private val IMAGEURL = "imageUrl"

    private lateinit var SUT: FetchUserProfileUseCaseSync
    private lateinit var mUserProfileHttpEndpointSyncTd: UserProfileHttpEndpointSyncTd
    private lateinit var mUsersCacheTd: UsersCacheTd

    @Before
    fun setUp() {
        mUserProfileHttpEndpointSyncTd = UserProfileHttpEndpointSyncTd()
        mUsersCacheTd = UsersCacheTd()

        SUT = FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSyncTd, mUsersCacheTd)
    }

    // User id is passed onto the endpoint
    @Test
    fun profileSync_idPassedToEndpoint_successReturned() {
        SUT.fetchUserProfileSync(USERID)
        assertThat(mUserProfileHttpEndpointSyncTd.userId, `is`(USERID))
    }

    // If fetch user profile succeeds then returns success
    @Test
    fun profileSync_fetchUserProfile_returnsSuccess() {
        val result = SUT.fetchUserProfileSync(USERID)
        assertThat(result, `is`(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS))
    }

    // If fetch is success, user details should be cached
    @Test
    fun profileSync_fetchUserProfileSucceeds_userCached() {
        SUT.fetchUserProfileSync(USERID)
        val user = mUsersCacheTd.getUser(USERID)!!
        assertThat(user.fullName, `is`(FULLNAME))
        assertThat(user.userId, `is`(USERID))
        assertThat(user.imageUrl, `is`(IMAGEURL))
    }

    // If fetch user profile fails then return fail (general error, auth error, server error)
    @Test
    fun profileSync_fetchUserFails_generalErrorReturned() {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true
        val result = SUT.fetchUserProfileSync(USERID)
        assertThat(result, `is`(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE))
    }
    // If fetch fails, user details should not be cached
    @Test
    fun profileSync_fetchUserProfileGeneralError_userNotCached() {
        mUserProfileHttpEndpointSyncTd.mIsGeneralError = true
        SUT.fetchUserProfileSync(USERID)
        val user = mUsersCacheTd.getUser(USERID)!!
        assertThat(user.fullName, `is`(""))
    }

    @Test
    fun profileSync_fetchUserProfileAuthError_userNotCached() {
        mUserProfileHttpEndpointSyncTd.mIsAuthError = true
        SUT.fetchUserProfileSync(USERID)
        val user = mUsersCacheTd.getUser(USERID)!!
        assertThat(user.fullName, `is`(""))
    }

    @Test
    fun profileSync_fetchUserProfileServerError_userNotCached() {
        mUserProfileHttpEndpointSyncTd.mIsServerError = true
        SUT.fetchUserProfileSync(USERID)
        val user = mUsersCacheTd.getUser(USERID)!!
        assertThat(user.fullName, `is`(""))
    }

    // If network error, network error returned
    @Test
    fun profileSync_fetchUserProfileFailed_networkErrorReturned() {
        mUserProfileHttpEndpointSyncTd.mIsNetworkError = true
        val result = SUT.fetchUserProfileSync(USERID)
        assertThat(result, `is`(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR))
    }


    private inner class UserProfileHttpEndpointSyncTd : UserProfileHttpEndpointSync {

        lateinit var userId: String

        var mIsGeneralError = false
        var mIsAuthError = false
        var mIsServerError = false
        var mIsNetworkError = false

        override fun getUserProfile(userId: String): UserProfileHttpEndpointSync.EndpointResult {
            this.userId = userId

            when {
                mIsGeneralError -> return UserProfileHttpEndpointSync.EndpointResult(
                    UserProfileHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                    "", "", ""
                )
                mIsAuthError -> return UserProfileHttpEndpointSync.EndpointResult(
                    UserProfileHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                    "", "", ""
                )
                mIsServerError -> return UserProfileHttpEndpointSync.EndpointResult(
                    UserProfileHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                    "", "", ""
                )
                mIsNetworkError -> throw NetworkErrorException()
                else -> return UserProfileHttpEndpointSync.EndpointResult(
                    UserProfileHttpEndpointSync.EndpointResultStatus.SUCCESS,
                    USERID, FULLNAME, IMAGEURL
                )
            }
        }

    }

    private inner class UsersCacheTd : UsersCache {

        private lateinit var user: User

        override fun cacheUser(user: User) {
            this.user = user
        }

        override fun getUser(userId: String?): User? {
            if (user.userId == userId) {
                return user
            } else {
                return null
            }
        }

    }

}