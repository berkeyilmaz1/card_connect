# Tekrarlı Kişi Tespiti Özelliği — Analiz Raporu

## Genel Durum
Özellik tamamen implement edilmiş, tüm dosyalar mevcut ve çalışıyor.

---

## Kritik Dosyalar

| Katman | Dosya | Notlar |
|--------|-------|--------|
| Domain Model | `domain/contact/model/DuplicateContactGroup.kt` | `DuplicateMatchReason` enum da burada |
| Domain UC | `domain/contact/usecase/FindDuplicateContactsUseCase.kt` | Repository'ye delege eder |
| Domain UC | `domain/contact/usecase/MergeContactsUseCase.kt` | Repository'ye delege eder |
| Repository Interface | `domain/contact/ContactRepository.kt` | `findDuplicateContacts()`, `mergeContacts()` |
| Data Impl | `data/remote/ContactRepositoryImpl.kt` | Ana algoritma burada (676 satır) |
| ViewModel | `presentation/main/home/viewmodel/HomeViewModel.kt` | `DuplicateBottomSheetState` sealed class burada |
| UI | `presentation/main/home/HomeView.kt` | ModalBottomSheet entegrasyonu |
| UI Bileşeni | `presentation/main/home/widgets/DuplicateContactsBottomSheet.kt` | 480 satır, `PrimarySelection` sealed class burada |

---

## Uçtan Uca Akış

```
HomeViewModel.init()
  → fetchContacts() → Firebase'den kişiler
  → checkForDuplicates(contacts)
      State: Scanning
      → getInternalContacts() (ContentResolver)
      → FindDuplicateContactsUseCase()
          → findDuplicateContacts() — normalleştirme + eşleştirme
      → Found(groups) veya Hidden
  → ModalBottomSheet göster
  → onMergeApproved(group, primarySelection)
      → mergeContacts() — Firebase batch + cihaz rehberi
      → advanceToNextGroup() → sonraki veya Hidden
  → fetchContacts() yeniden
```

---

## Algoritma Detayları (`ContactRepositoryImpl`)

**normalizePhone()** — Türkiye formatı:
- `90xx` (12 hane) → olduğu gibi bırak
- `0xxx` (11 hane) → `9xxx` yap
- 10 hane → `90` ekle

**findDuplicateContacts() (lines 412-530):**
1. Remote ve internal kişiler için telefon/email haritaları oluştur
2. `internalContactId` dolu remote'ların internal karşılıklarını hariç tut (zaten bağlı)
3. **Minimum 2 eşleşme şartı:** `if (remotes.size < 2 && internals.size < 2) continue`
4. `processedKeys` ile aynı grubu tekrar eklemeyi önle
5. Önce telefon geçişi, sonra email geçişi (telefonda işlenenleri atla)

**mergeContacts() (lines 532-629):**
1. Tüm phones/emails/websites/tags/socialMedias birleştir + tekilleştir
2. Firebase batch: primary güncelle, duplicate'leri sil
3. Cihaz: internal duplicate'leri `deleteInternalContacts()` ile sil
4. Cihaz: `ContactsHelper.addContactToPhone()` ile birleştirilmiş kişiyi ekle
5. Room cache'i güncelle

---

## State Yapısı

```kotlin
sealed class DuplicateBottomSheetState {
    Hidden, Scanning,
    Found(groups: List<DuplicateContactGroup>, currentIndex: Int = 0),
    Error(message: String)
}

sealed class PrimarySelection {
    Remote(contact: Contact),
    Internal(contact: InternalContact)
}
```

---

## String Kaynakları (strings.xml)

- `duplicate_contacts_found`, `duplicate_match_phone`, `duplicate_match_email`
- `merge_contacts`, `skip_group`, `select_primary`
- `duplicate_group_progress` (%1$d / %2$d Grup)
- `scanning_duplicates`, `internal_contact_badge`

---

## Bilinen Kısıtlamalar

1. **Sadece Türkiye telefon formatı** — uluslararası destek yok
2. **Minimum 2 eşleşme şartı** — tek telefon/emaili olan kişiler tespit edilemez
3. **Her uygulama açılışında çalışır** — önbellekleme yok
4. **ViewModel testleri yok** — `HomeViewModel` state geçişleri test edilmemiş
5. **Algoritma unit testi yok** — `ContactRepositoryImpl.findDuplicateContacts()` doğrudan test edilmemiş
6. **`fetchContacts()` erken çıkış sorunu** — contacts boşsa `checkForDuplicates` çağrılmaz (satır 149)
