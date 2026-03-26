package com.berkeyilmaz.cardapp.domain.scan

import com.berkeyilmaz.cardapp.domain.scan_result.model.SocialMedia
import com.berkeyilmaz.cardapp.domain.scan_result.model.SocialMediaPlatform

data class QrExtractedData(
    val phones: List<String> = emptyList(),
    val emails: List<String> = emptyList(),
    val websites: List<String> = emptyList(),
    val socialMedias: List<SocialMedia> = emptyList(),
    val vcardName: String? = null,
    val vcardOrganization: String? = null,
    val vcardTitle: String? = null,
    val vcardAddress: String? = null,
    val plainTextNote: String? = null
)

object QrContentParser {

    fun parse(rawValue: String): QrExtractedData {
        val trimmed = rawValue.trim()
        return when {
            trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) -> parseVCard(trimmed)
            trimmed.startsWith("tel:", ignoreCase = true) -> {
                val phone = trimmed.removePrefix("tel:").removePrefix("TEL:")
                QrExtractedData(phones = listOf(phone.trim()))
            }
            trimmed.startsWith("mailto:", ignoreCase = true) -> {
                val email = trimmed.removePrefix("mailto:").removePrefix("MAILTO:")
                    .substringBefore("?").trim()
                QrExtractedData(emails = listOf(email))
            }
            trimmed.startsWith("http://", ignoreCase = true) ||
                    trimmed.startsWith("https://", ignoreCase = true) -> {
                val platform = detectSocialMediaPlatform(trimmed)
                if (platform != null) {
                    QrExtractedData(socialMedias = listOf(SocialMedia(platform = platform, url = trimmed)))
                } else {
                    QrExtractedData(websites = listOf(trimmed))
                }
            }
            else -> QrExtractedData(plainTextNote = trimmed)
        }
    }

    private fun parseVCard(raw: String): QrExtractedData {
        val phones = mutableListOf<String>()
        val emails = mutableListOf<String>()
        val websites = mutableListOf<String>()
        var vcardName: String? = null
        var vcardOrganization: String? = null
        var vcardTitle: String? = null
        var vcardAddress: String? = null

        val lines = raw.lines()
        for (line in lines) {
            val upperLine = line.uppercase()
            when {
                upperLine.startsWith("FN:") -> {
                    vcardName = line.substringAfter(":").trim()
                }
                upperLine.startsWith("ORG:") -> {
                    vcardOrganization = line.substringAfter(":").trim()
                }
                upperLine.startsWith("TITLE:") -> {
                    vcardTitle = line.substringAfter(":").trim()
                }
                upperLine.startsWith("TEL") -> {
                    val phone = line.substringAfter(":").trim()
                    if (phone.isNotEmpty()) phones.add(phone)
                }
                upperLine.startsWith("EMAIL") -> {
                    val email = line.substringAfter(":").trim()
                    if (email.isNotEmpty()) emails.add(email)
                }
                upperLine.startsWith("URL:") -> {
                    val url = line.substringAfter(":").trim()
                    if (url.isNotEmpty()) {
                        val platform = detectSocialMediaPlatform(url)
                        if (platform != null) {
                            // handled in merge as website; caller merges via QrExtractedData
                        }
                        websites.add(url)
                    }
                }
                upperLine.startsWith("ADR") -> {
                    val adr = line.substringAfter(":").trim()
                    if (adr.isNotEmpty()) {
                        vcardAddress = adr.replace(";", ", ").trim().trimStart(',').trim()
                    }
                }
            }
        }

        return QrExtractedData(
            phones = phones,
            emails = emails,
            websites = websites,
            vcardName = vcardName,
            vcardOrganization = vcardOrganization,
            vcardTitle = vcardTitle,
            vcardAddress = vcardAddress
        )
    }

    private fun detectSocialMediaPlatform(url: String): SocialMediaPlatform? {
        val lower = url.lowercase()
        return when {
            "linkedin.com" in lower -> SocialMediaPlatform.LINKEDIN
            "instagram.com" in lower -> SocialMediaPlatform.INSTAGRAM
            "twitter.com" in lower || "x.com" in lower -> SocialMediaPlatform.TWITTER
            "facebook.com" in lower || "fb.com" in lower -> SocialMediaPlatform.FACEBOOK
            "youtube.com" in lower || "youtu.be" in lower -> SocialMediaPlatform.YOUTUBE
            "tiktok.com" in lower -> SocialMediaPlatform.TIKTOK
            "snapchat.com" in lower -> SocialMediaPlatform.SNAPCHAT
            "pinterest.com" in lower -> SocialMediaPlatform.PINTEREST
            "reddit.com" in lower -> SocialMediaPlatform.REDDIT
            "wa.me" in lower || "whatsapp.com" in lower -> SocialMediaPlatform.WHATSAPP
            "t.me" in lower || "telegram.me" in lower -> SocialMediaPlatform.TELEGRAM
            else -> null
        }
    }
}
