package com.touhid.composeform.database

import com.touhid.composeform.model.AppError
import com.touhid.composeform.model.AppResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class SafeDbCallTest {

    @Test
    fun `returns Success with the block's value`() = runBlocking {
        val result = safeDbCall(Dispatchers.Unconfined) { 42 }

        assertEquals(AppResult.Success(42), result)
    }

    @Test
    fun `maps an exception to Unknown keeping it as cause`() = runBlocking {
        val failure = IllegalStateException("disk I/O error")

        val result = safeDbCall(Dispatchers.Unconfined) { throw failure }

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Unknown)
        assertSame(failure, (error as AppError.Unknown).cause)
    }

    @Test(expected = CancellationException::class)
    fun `rethrows CancellationException`() {
        runBlocking { safeDbCall(Dispatchers.Unconfined) { throw CancellationException("cancelled") } }
    }
}
