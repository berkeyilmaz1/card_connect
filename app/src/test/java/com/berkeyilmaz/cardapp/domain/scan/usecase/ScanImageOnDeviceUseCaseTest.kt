package com.berkeyilmaz.cardapp.domain.scan.usecase

import com.berkeyilmaz.cardapp.domain.scan.ScanRepository
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.File

class ScanImageOnDeviceUseCaseTest {

    private lateinit var repository: ScanRepository
    private lateinit var useCase: ScanImageOnDeviceUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = ScanImageOnDeviceUseCase(repository)
    }

    @Test
    fun `invoke delegates to repository scanImageOnDevice`() = runTest {
        val file = mock<File>()
        val expected = Result.success(ScanResponse())
        whenever(repository.scanImageOnDevice(file)).thenReturn(expected)

        val result = useCase(file)

        assertEquals(expected, result)
        verify(repository).scanImageOnDevice(file)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val file = mock<File>()
        val failure = Result.failure<ScanResponse>(Exception("Scan failed"))
        whenever(repository.scanImageOnDevice(file)).thenReturn(failure)

        val result = useCase(file)

        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke returns success result`() = runTest {
        val file = mock<File>()
        val scanResponse = ScanResponse()
        whenever(repository.scanImageOnDevice(file)).thenReturn(Result.success(scanResponse))

        val result = useCase(file)

        assertTrue(result.isSuccess)
    }
}
