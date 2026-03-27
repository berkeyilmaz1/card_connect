package com.berkeyilmaz.cardapp.domain.scan.usecase

import com.berkeyilmaz.cardapp.domain.scan.QrContentParser
import com.berkeyilmaz.cardapp.domain.scan.QrExtractedData
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.domain.scan_result.model.SocialMedia
import javax.inject.Inject

class MergeQrIntoScanResponseUseCase @Inject constructor() {

    operator fun invoke(base: ScanResponse, qrRawValues: List<String>): ScanResponse {
        if (qrRawValues.isEmpty()) return base
        var merged = base
        for (raw in qrRawValues) {
            val qrData = QrContentParser.parse(raw)
            merged = mergeOne(merged, qrData)
        }
        return merged
    }

    private fun mergeOne(base: ScanResponse, qr: QrExtractedData): ScanResponse {
        val newPhones = qr.phones.filter { qrPhone ->
            val normalized = normalizePhone(qrPhone)
            base.phones.none { normalizePhone(it) == normalized }
        }

        val newEmails = qr.emails.filter { qrEmail ->
            val normalized = qrEmail.lowercase().trim()
            base.emails.none { it.lowercase().trim() == normalized }
        }

        val newWebsites = qr.websites.filter { qrUrl ->
            val normalized = normalizeUrl(qrUrl)
            base.websites.none { normalizeUrl(it) == normalized }
        }

        val newSocialMedias = qr.socialMedias.filter { qrSm ->
            base.socialMedias.none { existing ->
                existing.url?.lowercase()?.trimEnd('/') == qrSm.url?.lowercase()?.trimEnd('/') ||
                        (existing.platform == qrSm.platform && existing.url != null && qrSm.url != null)
            }
        }

        val mergedNote = mergeNotes(base.note, qr.plainTextNote)

        return base.copy(
            fullName = if (base.fullName.isNullOrBlank()) qr.vcardName else base.fullName,
            organization = if (base.organization.isNullOrBlank()) qr.vcardOrganization else base.organization,
            title = if (base.title.isNullOrBlank()) qr.vcardTitle else base.title,
            address = if (base.address.isNullOrBlank()) qr.vcardAddress else base.address,
            phones = base.phones + newPhones,
            emails = base.emails + newEmails,
            websites = base.websites + newWebsites,
            socialMedias = base.socialMedias + newSocialMedias,
            note = mergedNote
        )
    }

    private fun normalizePhone(phone: String): String {
        return phone.replace(Regex("[\\s\\-().+]"), "")
    }

    private fun normalizeUrl(url: String): String {
        return url.lowercase().trimEnd('/')
    }

    private fun mergeNotes(existing: String?, addition: String?): String? {
        if (addition.isNullOrBlank()) return existing
        if (existing.isNullOrBlank()) return addition
        if (existing.contains(addition)) return existing
        return "$existing\n$addition"
    }
}
