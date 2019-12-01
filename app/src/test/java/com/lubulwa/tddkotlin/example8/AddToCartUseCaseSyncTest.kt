package com.lubulwa.tddkotlin.example8

import com.lubulwa.tddkotlin.example8.AddToCartUseCaseSync.*
import com.lubulwa.tddkotlin.example8.networking.AddToCartHttpEndpointSync
import com.lubulwa.tddkotlin.example8.networking.CartItemScheme
import com.lubulwa.tddkotlin.example8.networking.NetworkErrorException
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddToCartUseCaseSyncTest {

    // region constants ----------------------------------------------------------------------------
    private val OFFERID = "offerId"
    private val AMOUNT = 4
    // endRegion constants -------------------------------------------------------------------------

    // region helper fields ------------------------------------------------------------------------
    @Mock
    private lateinit var mAddToCartHttpEndpointSync: AddToCartHttpEndpointSync
    // endRegion helper fields ---------------------------------------------------------------------

    @Captor
    private lateinit var acCartItem: ArgumentCaptor<CartItemScheme>

    private lateinit var SUT: AddToCartUseCaseSync

    @Before
    fun setUp() {
        SUT = AddToCartUseCaseSync(mAddToCartHttpEndpointSync)

        success()
    }

    // correct parameters passed to the endpoint
    @Test
    fun addToCartSync_correctParametersPassedToEndpoint() {
        SUT.addToCartSync(OFFERID, AMOUNT)
        verify(mAddToCartHttpEndpointSync).addToCartSync(capture(acCartItem))
        assertThat(acCartItem.value.offerId, `is`(OFFERID))
        assertThat(acCartItem.value.amount, `is`(AMOUNT))
    }

    // endpoint success - success returned
    @Test
    fun addToCartSync_success_successReturned() {
        val result = SUT.addToCartSync(OFFERID, AMOUNT)
        assertThat(result, `is`(UseCaseResult.SUCCESS))
    }

    // endpoint auth error - failure returned
    @Test
    fun addToCartSync_authError_failureReturned() {
        authError()
        val result = SUT.addToCartSync(OFFERID, AMOUNT)
        assertThat(result, `is`(UseCaseResult.FAILURE))
    }

    // endpoint general error - failure returned
    @Test
    fun addToCartSync_generalError_failureReturned() {
        generalError()
        val result = SUT.addToCartSync(OFFERID, AMOUNT)
        assertThat(result, `is`(UseCaseResult.FAILURE))
    }

    // network exception - network error returned
    @Test
    fun addToCartSync_networkError_failureReturned() {
        networkError()
        val result = SUT.addToCartSync(OFFERID, AMOUNT)
        assertThat(result, `is`(UseCaseResult.NETWORK_ERROR))
    }

    // region helper methods -----------------------------------------------------------------------

    private fun success() {
        whenever(mAddToCartHttpEndpointSync.addToCartSync(any())).thenReturn(AddToCartHttpEndpointSync.EndpointResult.SUCCESS)
    }

    private fun authError() {
        whenever(mAddToCartHttpEndpointSync.addToCartSync(any())).thenReturn(AddToCartHttpEndpointSync.EndpointResult.AUTH_ERROR)
    }

    private fun generalError() {
        whenever(mAddToCartHttpEndpointSync.addToCartSync(any())).thenReturn(AddToCartHttpEndpointSync.EndpointResult.GENERAL_ERROR)
    }

    private fun networkError() {
        whenever(mAddToCartHttpEndpointSync.addToCartSync(any())).doThrow(NetworkErrorException())
    }

    // endRegion helper methods --------------------------------------------------------------------

    // region helper classes -----------------------------------------------------------------------
    // endRegion helper classes --------------------------------------------------------------------

}