package com.lubulwa.tddkotlin.example6

import com.lubulwa.tddkotlin.example4.authtoken.AuthTokenCache
import com.lubulwa.tddkotlin.example4.eventbus.EventBusPoster
import com.lubulwa.tddkotlin.example4.eventbus.LoggedInEvent
import com.lubulwa.tddkotlin.example4.networking.LoginHttpEndpointSync
import com.lubulwa.tddkotlin.example4.networking.NetworkErrorException
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginUseCaseSyncMockTest {

    private val USERNAME = "username"
    private val PASSWORD = "password"
    private val AUTHTOKEN = "authToken"

    private lateinit var SUT: LoginUseCaseSyncMock

    @Mock
    private lateinit var mLoginHttpEndpointSyncMock: LoginHttpEndpointSync

    @Mock
    private lateinit var mAuthTokenCacheMock: AuthTokenCache

    @Mock
    private lateinit var mEventBusPosterTdMock: EventBusPoster

    @Captor
    lateinit var ac: ArgumentCaptor<String>


    @Before
    fun setUp() {
        SUT = LoginUseCaseSyncMock(mLoginHttpEndpointSyncMock, mAuthTokenCacheMock, mEventBusPosterTdMock)

        success()
    }

    @Test
    fun loginSync_success_usernameAndPasswordPassedToEndpoint() {
        SUT.loginSync(USERNAME, PASSWORD)
        verify(mLoginHttpEndpointSyncMock, times(1)).loginSync(capture(ac), capture(ac))
        val captures = ac.allValues
        assertThat(captures[0], `is`(USERNAME))
        assertThat(captures[1], `is`(PASSWORD))
    }

    @Test
    fun loginSync_success_authTokenCached() {
        SUT.loginSync(USERNAME, PASSWORD)
        verify(mAuthTokenCacheMock).cacheAuthToken(capture(ac))
        assertThat(ac.value, `is`(AUTHTOKEN))
    }

    @Test
    fun loginSync_generalError_authTokenNotCached() {
        generalError()
        SUT.loginSync(USERNAME, USERNAME)
        verifyNoMoreInteractions(mAuthTokenCacheMock)
    }

    @Test
    fun loginSyn_success_loggedInEventPosted() {
        SUT.loginSync(USERNAME, PASSWORD)
        verify(mEventBusPosterTdMock).postEvent(capture(ac))
        assertThat(ac.value, `is`(instanceOf(LoggedInEvent::class.java)))
    }

    @Test
    fun loginSync_success_successReturned() {
        val result = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(result, `is`(LoginUseCaseSyncMock.UseCaseResult.SUCCESS))
    }

    @Test
    fun loginSync_networkError_networkErrorReturned() {
        networkError()
        val result = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(result, `is`(LoginUseCaseSyncMock.UseCaseResult.NETWORK_ERROR))
    }

    private fun networkError() {
        `when`(mLoginHttpEndpointSyncMock.loginSync(anyString(), anyString())).doThrow(NetworkErrorException())
    }

    private fun success() {
        `when`(mLoginHttpEndpointSyncMock.loginSync(anyString(), anyString())).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.SUCCESS,
                AUTHTOKEN
            )
        )
    }

    private fun generalError() {
        `when`(mLoginHttpEndpointSyncMock.loginSync(anyString(), anyString())).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                ""
            )
        )
    }
}