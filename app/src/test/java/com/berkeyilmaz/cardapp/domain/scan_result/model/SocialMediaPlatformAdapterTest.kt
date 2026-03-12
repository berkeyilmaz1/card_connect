package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.JsonPrimitive
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SocialMediaPlatformAdapterTest {

    private lateinit var adapter: SocialMediaPlatformAdapter

    @Before
    fun setUp() {
        adapter = SocialMediaPlatformAdapter()
    }

    @Test
    fun `serialize returns platformName as JsonPrimitive`() {
        val result = adapter.serialize(SocialMediaPlatform.LINKEDIN, null, null)
        assertEquals("LinkedIn", result.asString)
    }

    @Test
    fun `serialize OTHER returns Other platformName`() {
        val result = adapter.serialize(SocialMediaPlatform.OTHER, null, null)
        assertEquals("Other", result.asString)
    }

    @Test
    fun `deserialize LinkedIn returns LINKEDIN`() {
        val json = JsonPrimitive("LinkedIn")
        assertEquals(SocialMediaPlatform.LINKEDIN, adapter.deserialize(json, null, null))
    }

    @Test
    fun `deserialize unknown returns OTHER`() {
        val json = JsonPrimitive("UnknownPlatform")
        assertEquals(SocialMediaPlatform.OTHER, adapter.deserialize(json, null, null))
    }

    @Test
    fun `deserialize null returns OTHER`() {
        assertEquals(SocialMediaPlatform.OTHER, adapter.deserialize(null, null, null))
    }

    @Test
    fun `deserialize Twitter returns TWITTER`() {
        val json = JsonPrimitive("Twitter")
        assertEquals(SocialMediaPlatform.TWITTER, adapter.deserialize(json, null, null))
    }

    @Test
    fun `deserialize Facebook returns FACEBOOK`() {
        val json = JsonPrimitive("Facebook")
        assertEquals(SocialMediaPlatform.FACEBOOK, adapter.deserialize(json, null, null))
    }

    @Test
    fun `deserialize Instagram returns INSTAGRAM`() {
        val json = JsonPrimitive("Instagram")
        assertEquals(SocialMediaPlatform.INSTAGRAM, adapter.deserialize(json, null, null))
    }

    @Test
    fun `SocialMediaPlatform has 12 entries`() {
        assertEquals(12, SocialMediaPlatform.entries.size)
    }

    @Test
    fun `all platforms serialize and deserialize correctly`() {
        SocialMediaPlatform.entries.filter { it != SocialMediaPlatform.OTHER }.forEach { platform ->
            val json = adapter.serialize(platform, null, null)
            val result = adapter.deserialize(json, null, null)
            assertEquals(platform, result)
        }
    }
}
