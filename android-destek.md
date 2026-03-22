# Android Desteği

## 1. Android Sürüm Desteği

- **minSdk**: 26 → Android 8.0 Oreo (minimum desteklenen sürüm)
- **targetSdk**: 36 → Android 16
- **compileSdk**: 36 → Android 16
- **JVM hedef**: Java 11 (sourceCompatibility / targetCompatibility)
- Desteklenen cihaz yelpazesi: Android 8.0 ve üzeri

---

## 3. Kullanılan İzinler

| İzin | Amaç |
|------|------|
| `CAMERA` | Kartvizit tarama için kamera erişimi |
| `WRITE_EXTERNAL_STORAGE` (maxSdk 28) | Android 9 ve altında dosya yazma |
| `READ_CONTACTS` | Cihaz rehberinden kişi okuma |
| `WRITE_CONTACTS` | Cihaz rehberine kişi ekleme / düzenleme |

- **Donanım gereksinimi**: `android.hardware.camera.any` (arka veya ön kamera zorunlu)

---

## 4. Donanım ve Sistem Gereksinimleri

- **Kamera**: Zorunlu (`uses-feature` ile beyan edilmiş)
- **İnternet**: Firebase, Retrofit ve Gemini AI servisleri için zorunlu;
- **Yerel depolama**: Room veritabanı ve MediaPipe model dosyaları için gerekli

---

## 5. Desteklenen Cihaz Türleri

- **Birincil hedef**: Akıllı Android telefon
- **Tablet**: Manifest'te özel kısıtlama yok; tablet çalışması muhtemel ancak test edilmemiş
- **Orientation**: Manifest'te kilitli orientation tanımı yok; varsayılan sistem yönü geçerli
- **Çoklu dil**: `localeConfig` ile dil desteği beyan edilmiş

---

## 6. Test ve Çalışma Gereksinimleri

- **İnternet bağlantısı**: Zorunlu (Firebase Auth, Firestore, Gemini AI, Retrofit)
- **Google Play Services**: Zorunlu (Firebase, Credential Manager, Play Integrity)
- **Minimum Android sürümü**: 8.0 (API 26)
- **Kamera donanımı**: Zorunlu
- **Test altyapısı**: JUnit 4, Mockito 5, kotlinx-coroutines-test, Espresso, Compose UI Test
- **Hugging Face API Token**: `local.properties` üzerinden `BuildConfig`'e aktarılır; ilgili özelliklerin çalışması için gereklidir
