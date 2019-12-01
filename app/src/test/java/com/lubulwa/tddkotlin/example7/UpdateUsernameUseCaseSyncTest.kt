package com.lubulwa.tddkotlin.example7

import com.lubulwa.tddkotlin.example7.eventbus.EventBusPoster
import com.lubulwa.tddkotlin.example7.eventbus.UserDetailsChangedEvent
import com.lubulwa.tddkotlin.example7.networking.NetworkErrorException
import com.lubulwa.tddkotlin.example7.networking.UpdateUsernameHttpEndpointSync
import com.lubulwa.tddkotlin.example7.users.User
import com.lubulwa.tddkotlin.example7.users.UsersCache
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UpdateUsernameUseCaseSyncTest {

    private val USERNAME = "username"
    private val PASSWORD = "password"
    private val USERID = "userId"

    private lateinit var SUT: UpdateUsernameUseCaseSync

    @Mock
    private lateinit var mUpdateUsernameHttpEndpointSync: UpdateUsernameHttpEndpointSync

    @Mock
    private lateinit var mUsersCache: UsersCache

    @Mock
    private lateinit var mEventBusPoster: EventBusPoster

    @Captor
    private lateinit var acString: ArgumentCaptor<String>

    @Captor
    private lateinit var acUser: ArgumentCaptor<User>

    @Captor
    private lateinit var acEvent: ArgumentCaptor<Any>

    @Before
    fun setUp() {
        SUT = UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSync, mUsersCache, mEventBusPoster)

        success()
    }

    @Test
    fun updateUsername_success_userIdAndUsernamePassedToEndpoint() {
        SUT.updateUsernameSync(USERID, USERNAME)
        verify(mUpdateUsernameHttpEndpointSync).updateUsername(capture(acString), capture(acString))
        val captures = acString.allValues
        assertThat(captures[0], `is`(USERID))
        assertThat(captures[1], `is`(USERNAME))
    }

    @Test
    fun updateUsername_success_userIsCached() {
        SUT.updateUsernameSync(USERID, USERNAME)
        verify(mUsersCache).cacheUser(capture(acUser))
        val user = acUser.value
        assertThat(user.userId, `is`(USERID))
        assertThat(user.username, `is`(USERNAME))
    }

    @Test
    fun updateUsername_success_userIsNotCached() {
        generalError()
        SUT.updateUsernameSync(USERID, USERNAME)
        verifyNoMoreInteractions(mUsersCache)
    }

    @Test
    fun updateUsername_success_postToEventBus() {
        SUT.updateUsernameSync(USERID, USERNAME)
        verify(mEventBusPoster).postEvent(capture(acEvent))
        assertThat(acEvent.value, `is`(instanceOf(UserDetailsChangedEvent::class.java)))
    }

    @Test
    fun updateUsername_postToEventBus_userReturned() {
        SUT.updateUsernameSync(USERID, USERNAME)
        verify(mEventBusPoster).postEvent(capture(acEvent))
        val event = acEvent.value as UserDetailsChangedEvent

        assertThat(event.user.userId, `is`(USERID))
    }



    @Test
    fun updateUsername_success_successReturned() {
        val result = SUT.updateUsernameSync(USERID, USERNAME)
        assertThat(result, `is`(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS))
    }

    @Test
    fun updateUsername_generalError_errorReturned() {
        generalError()
        val result = SUT.updateUsernameSync(USERID, USERNAME)
        assertThat(result, `is`(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    fun updateUsername_authError_errorReturned() {
        authError()
        val result = SUT.updateUsernameSync(USERID, USERNAME)
        assertThat(result, `is`(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    fun updateUsername_serverError_errorReturned() {
        serverError()
        val result = SUT.updateUsernameSync(USERID, USERNAME)
        assertThat(result, `is`(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    fun updateUsername_networkError_errorReturned() {
        networkError()
        val result = SUT.updateUsernameSync(USERID, USERNAME)
        assertThat(result, `is`(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR))
    }

    private fun success() {
        `when`(mUpdateUsernameHttpEndpointSync.updateUsername(USERID, USERNAME)).thenReturn(
            UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS,
                USERID,
                USERNAME
            )
        )
    }

    private fun generalError() {
        `when`(mUpdateUsernameHttpEndpointSync.updateUsername(USERID, USERNAME)).thenReturn(
            UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                "",
                ""
            )
        )
    }

    private fun authError() {
        `when`(mUpdateUsernameHttpEndpointSync.updateUsername(USERID, USERNAME)).thenReturn(
            UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                "",
                ""
            )
        )
    }

    private fun serverError() {
        `when`(mUpdateUsernameHttpEndpointSync.updateUsername(USERID, USERNAME)).thenReturn(
            UpdateUsernameHttpEndpointSync.EndpointResult(
                UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                "",
                ""
            )
        )
    }

    private fun networkError() {
        `when`(mUpdateUsernameHttpEndpointSync.updateUsername(USERID, USERNAME)).doThrow(NetworkErrorException())
    }
}