package com.example.imagegallery.data.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.MalformedURLException
import java.net.URL

@RunWith(RobolectricTestRunner::class)
class RequestTest {

    @Test
    fun testGetURL() {
        val subject = Request(URL_STRING, Method.GET)
        assertEquals(URL(URL_STRING), subject.getURL())
    }


    @Test(expected = MalformedURLException::class)
    fun testGetURLMalformedUrl() {
        val subject = Request("//", Method.GET)
        subject.getURL()
    }

    @Test
    fun testGetURLWithQueryParameters() {
        val subject =
            Request(URL_STRING, Method.GET, mapOf("param1" to "value1", "param2" to "value2"))
        val url = subject.getURL()
        assertNotNull(url)
        assertEquals("/service", url.path)
        assertEquals("param1=value1&param2=value2", url.query)
    }

    companion object {
        const val URL_STRING = "http://test.com/service"
    }
}
