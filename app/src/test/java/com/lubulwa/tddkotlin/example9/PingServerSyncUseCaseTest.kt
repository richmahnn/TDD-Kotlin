package com.lubulwa.tddkotlin.example9

import org.hamcrest.CoreMatchers.`is`
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PingServerSyncUseCaseTest {

    lateinit var SUT: PingServerSyncUseCase

    @Mock
    lateinit var pingServerHttpEndpointSync: PingServerHttpEndpointSync

    @Before
    fun setUp() {
        SUT = PingServerSyncUseCase()

        success()
    }

    @Test
    fun pingServer_success_successReturned() {
        val result = SUT.pingServer()
        assertThat(result, `is`(PingServerSyncUseCase.UseCaseResult.SUCCESS))
    }

    private fun success() {
        `when`(pingServerHttpEndpointSync.pingServerSync()).thenReturn(PingServerHttpEndpointSync.EndpointResult.SUCCESS)
    }
}