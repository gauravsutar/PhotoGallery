package com.example.imagegallery.data.api

import com.example.imagegallery.FileReader
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ApiServiceTest {

    private val subject = ApiService()

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSuccessfulResponse() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(200)
        mockedResponse.setBody(FileReader.readStringFromFile(path = "json/success.json"))
        mockWebServer.enqueue(mockedResponse)
        val url = mockWebServer.url("/services/rest")
        val data = subject.getData(Request(url.toString(), Method.GET))
        assertNotNull(data)
    }

    @Test
    fun testFailedResponse() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(500)
        mockWebServer.enqueue(mockedResponse)
        val url = mockWebServer.url("/services/rest")
        val data = subject.getData(Request(url.toString(), Method.GET))
        assertNull(data)
    }
}
