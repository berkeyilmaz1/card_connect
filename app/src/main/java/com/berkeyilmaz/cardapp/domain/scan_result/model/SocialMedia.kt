package com.berkeyilmaz.cardapp.domain.scan_result.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.lang.reflect.Type

data class SocialMedia(
    @SerializedName("platform") var platform: SocialMediaPlatform? = null,
    @SerializedName("url") var url: String? = null
) : Serializable

@JsonAdapter(SocialMediaPlatformAdapter::class)
enum class SocialMediaPlatform(val platformName: String) {
    FACEBOOK("Facebook"),
    TWITTER("Twitter"),
    LINKEDIN("LinkedIn"),
    INSTAGRAM("Instagram"),
    YOUTUBE("YouTube"),
    TIKTOK("TikTok"),
    SNAPCHAT("Snapchat"),
    PINTEREST("Pinterest"),
    REDDIT("Reddit"),
    WHATSAPP("WhatsApp"),
    TELEGRAM("Telegram"),
    OTHER("Other")
}

class SocialMediaPlatformAdapter : JsonSerializer<SocialMediaPlatform>,
    JsonDeserializer<SocialMediaPlatform> {

    override fun serialize(
        src: SocialMediaPlatform?, typeOfSrc: Type?, context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.platformName)
    }

    override fun deserialize(
        json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
    ): SocialMediaPlatform {
        val name = json?.asString
        return SocialMediaPlatform.entries.firstOrNull { it.platformName == name }
            ?: SocialMediaPlatform.OTHER
    }
}