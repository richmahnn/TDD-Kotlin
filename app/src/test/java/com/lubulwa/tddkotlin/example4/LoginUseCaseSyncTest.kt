package com.lubulwa.tddkotlin.example4

import com.lubulwa.tddkotlin.example4.authtoken.AuthTokenCache
import com.lubulwa.tddkotlin.example4.eventbus.EventBusPoster
import com.lubulwa.tddkotlin.example4.eventbus.LoggedInEvent
import com.lubulwa.tddkotlin.example4.networking.LoginHttpEndpointSync
import com.lubulwa.tddkotlin.example4.networking.NetworkErrorException
import org.hamcrest.CoreMatchers.*
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class LoginUseCaseSyncTest { // We are going to use test doubles(stubs)

    private val USERNAME = "username"
    private val PASSWORD = "password"
    private val AUTHTOKEN = "authToken"

    private lateinit var SUT: LoginUseCaseSync

    private lateinit var mLoginHttpEndpointSyncTd: LoginHttpEndpointSyncTd
    private lateinit var mAuthTokenCacheTd: AuthTokenCacheTd
    private lateinit var mEventBusPosterTd: EventBusPosterTd

    @Before
    fun setUp() {
        mLoginHttpEndpointSyncTd = LoginHttpEndpointSyncTd()
        mAuthTokenCacheTd = AuthTokenCacheTd()
        mEventBusPosterTd =EventBusPosterTd()

        SUT = LoginUseCaseSync(mLoginHttpEndpointSyncTd, mAuthTokenCacheTd, mEventBusPosterTd)
    }

    // Username and password are passed to the endpoint
    @Test
    fun loginSync_usernameAndPasswordPassedToEndpoint_successReturned() {
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mLoginHttpEndpointSyncTd.username, `is`(USERNAME))
        assertThat(mLoginHttpEndpointSyncTd.password, `is`(PASSWORD))
    }

    // If login succeeds - token must be cached
    @Test
    fun loginSync_success_tokenIsCached() {
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mAuthTokenCacheTd.authToken, `is`(AUTHTOKEN))
    }

    // If login fails - auth token is not cached
    @Test
    fun loginSync_generalError_authTokenNotCached() {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mAuthTokenCacheTd.token, `is`(""))
    }

    @Test
    fun loginSync_authError_authTokenNotCached() {
        mLoginHttpEndpointSyncTd.mIsAuthError = true
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mAuthTokenCacheTd.token, `is`(""))
    }

    @Test
    fun loginSync_serverError_authTokenNotCached() {
        mLoginHttpEndpointSyncTd.mIsServerError = true
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mAuthTokenCacheTd.token, `is`(""))
    }

    // If login succeeds - login event is posted to event bus
    @Test
    fun loginSync_success_loginEventPosted() {
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mEventBusPosterTd.mEvent, `is`(instanceOf(LoggedInEvent::class.java)))
    }

    // If login fails - no login event posted
    @Test
    fun loginSync_generalError_noInteractionWithEventbusPoster() {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mEventBusPosterTd.mIteractionsCount, `is`(0))
    }

    @Test
    fun loginSync_authError_noInteractionWithEventbusPoster() {
        mLoginHttpEndpointSyncTd.mIsAuthError = true
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mEventBusPosterTd.mIteractionsCount, `is`(0))
    }

    @Test
    fun loginSync_serverError_noInteractionWithEventbusPoster() {
        mLoginHttpEndpointSyncTd.mIsServerError = true
        SUT.loginSync(USERNAME, PASSWORD)
        assertThat(mEventBusPosterTd.mIteractionsCount, `is`(0))
    }

    // If login succeeds - success returned
    @Test
    fun loginSync_success_successReturned() {
        val result = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(result, `is`(LoginUseCaseSync.UseCaseResult.SUCCESS))
    }

    // If login fails - fails  returned
    @Test
    fun loginSync_generalError_failureReturned() {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true
        val result = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(result, `is`(LoginUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    fun loginSync_authError_failureReturned() {
        mLoginHttpEndpointSyncTd.mIsAuthError = true
        val result = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(result, `is`(LoginUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    fun loginSync_serverError_failureReturned() {
        mLoginHttpEndpointSyncTd.mIsServerError = true
        val result = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(result, `is`(LoginUseCaseSync.UseCaseResult.FAILURE))
    }

    // If network - network error returned
    @Test
    fun loginSync_networkError_networkErrorReturned() {
        mLoginHttpEndpointSyncTd.mIsNetworkError = true
        val result = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(result, `is`(LoginUseCaseSync.UseCaseResult.NETWORK_ERROR))
    }


    //----------------------------------------------------------------------------------------------------------------
    //---------------------------------------------- HELPER CLASSES --------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------
    private inner class LoginHttpEndpointSyncTd : LoginHttpEndpointSync { // Td stands for test double

        lateinit var username: String
        lateinit var password: String

        var mIsGeneralError: Boolean = false
        var mIsAuthError: Boolean = false
        var mIsServerError: Boolean = false
        var mIsNetworkError: Boolean = false

        override fun loginSync(
            username: String,
            password: String
        ): LoginHttpEndpointSync.EndpointResult {
            this.username = username
            this.password = password

            if (mIsGeneralError) {
                return LoginHttpEndpointSync.EndpointResult(
                    LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                    ""
                )
            } else if (mIsAuthError) {
                return LoginHttpEndpointSync.EndpointResult(
                    LoginHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                    ""
                )
            } else if (mIsServerError) {
                return LoginHttpEndpointSync.EndpointResult(
                    LoginHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                    ""
                )
            } else if (mIsNetworkError) {
                throw NetworkErrorException()
            } else {
                return LoginHttpEndpointSync.EndpointResult(
                    LoginHttpEndpointSync.EndpointResultStatus.SUCCESS,
                    AUTHTOKEN
                )
            }
        }

    }

    private inner class AuthTokenCacheTd : AuthTokenCache {

        var token = ""

        override val authToken: String
            get() = token

        override fun cacheAuthToken(authToken: String) {
            token = authToken
        }

    }

    private inner class EventBusPosterTd : EventBusPoster {

        lateinit var mEvent: Any

        var mIteractionsCount = 0

        override fun postEvent(event: Any) {
            mIteractionsCount ++
            mEvent = event
        }

    }

}