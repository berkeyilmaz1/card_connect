# Test Raporu: CardConnect Android Uygulaması Birim Testleri

---

## Genel Bakış

Toplamda **25 test dosyası** ve **157 test** yazıldı. Testler Clean Architecture katmanlarına göre organize edilmiştir: Core, Data ve Domain katmanları. ViewModels ve Firebase implementasyonları Android runtime bağımlılığı gerektirdiğinden unit test kapsamı dışında tutulmuştur.

---

## 1. CORE Katmanı

### `ResponseStateTest` (9 test)
**Dosya:** `core/common/ResponseStateTest.kt`

`ResponseState` sealed class'ını test eder. Bu class, tüm async operasyonların sonucunu temsil eden merkezi bir wrapper'dır.

- `Success` state'inin `data` ve `message` alanlarını doğru taşıyıp taşımadığı
- `message` parametresinin varsayılan olarak `null` geldiği
- `Error` state'inin hata mesajını doğru taşıyıp taşımadığı
- `Success` ve `Error`'ın `ResponseState` türünün örnekleri olup olmadığı (polimorfizm)
- Aynı değerlere sahip iki `Success` veya `Error` nesnesinin birbirine eşit olduğu (data class equality)
- Farklı mesajlara sahip `Error`'ların eşit olmadığı

---

### `EmptyTextValidationRuleTest` (8 test)
**Dosya:** `core/validation/EmptyTextValidationRuleTest.kt`

Form doğrulama altyapısını test eder. `EmptyTextValidationRule`, kullanıcı girişlerinin boş olup olmadığını kontrol eder.

- Dolu bir metin için `validate()`'in `true` döndürdüğü
- Boş string için `validate()`'in `false` döndürdüğü
- Yalnızca boşluk içeren metinlerin (`"   "`) geçerli sayıldığı (boşluk ≠ boş)
- Tek karakter, uzun metin, sayısal metin için doğrulama davranışı
- `errorMessageRes` resource ID'sinin doğru atandığı
- `BaseValidationRule` interface'ini implement ettiği

---

## 2. DATA Katmanı

### `StringListConverterTest` (10 test)
**Dosya:** `data/local/converter/StringListConverterTest.kt`

Room veritabanının `List<String>` tipini SQLite'ın anlayacağı JSON formatına çevirip geri okuyan `TypeConverter`'ı test eder. Bu converter; telefon numaraları, email listesi ve website listesi gibi alanlar için kullanılır.

- `fromStringList()`'in listeyi JSON'a doğru dönüştürdüğü
- `toStringList()`'in JSON'u listeye geri doğru çevirdiği
- `null` liste için boş JSON array üretildiği
- Boş liste için boş JSON array üretildiği
- `null` JSON string için boş liste döndürüldüğü
- Boş JSON string için boş liste döndürüldüğü
- Telefon numaraları ve email adresleri gibi gerçek kullanım senaryolarında round-trip (dönüp gelme) tutarlılığı
- Tek elemanlı liste için round-trip tutarlılığı
- Manuel JSON array girdisinin doğru parse edildiği

---

### `InternalContactMapperTest` (11 test)
**Dosya:** `data/local/mapper/InternalContactMapperTest.kt`

Domain modeli (`InternalContact`) ile veritabanı entity'si (`InternalContactEntity`) arasındaki dönüşüm fonksiyonlarını test eder. Bu mapper, Clean Architecture'ın katmanlar arası veri izolasyonunu sağlar.

**`toEntity()` (Domain → DB):**
- Tüm alanların (contactId, fullName, phones, emails, websites, organization, title) doğru eşlendiği
- `null` email listesinin entity'de boş listeye dönüştürüldüğü
- `null` website listesinin entity'de boş listeye dönüştürüldüğü

**`toDomain()` (DB → Domain):**
- Tüm alanların domain modeline doğru eşlendiği
- Boş email listesinin domain'de `null`'a dönüştürüldüğü (tersine dönüşüm)
- Boş website listesinin domain'de `null`'a dönüştürüldüğü

**Liste dönüşümleri:**
- `toEntityList()` ile liste dönüşümünün doğruluğu
- `toDomainList()` ile liste dönüşümünün doğruluğu
- Tam round-trip: domain → entity → domain dönüşümünde veri kaybı olmadığı
- Boş listelerle liste dönüşümlerinin güvenli çalıştığı

---

## 3. DOMAIN Katmanı — Modeller

### `InternalContactChangesTest` (8 test)
**Dosya:** `domain/contact/model/InternalContactChangesTest.kt`

Telefon rehberindeki değişiklikleri izleyen `InternalContactChanges` data class'ını test eder. Bu model; eklenen, silinen ve değiştirilen kişileri takip eder.

- Tüm listeler boşsa `hasChanges`'ın `false` döndürdüğü
- Yalnızca `added` listesi doluysa `hasChanges`'ın `true` döndürdüğü
- Yalnızca `removed` listesi doluysa `hasChanges`'ın `true` döndürdüğü
- Yalnızca `modified` listesi doluysa `hasChanges`'ın `true` döndürdüğü
- Tüm listeler doluysa `hasChanges`'ın `true` döndürdüğü
- Varsayılan constructor'da tüm listelerin boş başladığı
- Data class equality (eşitlik) kontrolü

---

### `DuplicateContactGroupTest` (6 test)
**Dosya:** `domain/contact/model/DuplicateContactGroupTest.kt`

Mükerrer kişi tespitinde kullanılan `DuplicateContactGroup` modelini test eder.

- `PHONE`, `EMAIL`, `PHONE_AND_EMAIL` eşleşme sebepleriyle grup oluşturulabildiği
- `internalContacts` listesinin varsayılan olarak boş başladığı
- `internalContacts` listesinin doldurulabildiği
- `DuplicateMatchReason` enum'unun tam olarak 3 değer içerdiği

---

### `TagTest` (7 test) ve `TagCategoryTest` (11 test)
**Dosyalar:** `domain/scan_result/model/TagTest.kt`, `TagCategoryTest.kt`

Kişilere atanan etiket sistemini test eder. Kartlardan taranan kişiler otomatik kategorize edilir.

**`TagTest`:**
- `getCategoryEnum()` extension fonksiyonunun tag category string'inden doğru enum döndürdüğü
- Büyük/küçük harf duyarsızlığı
- Bilinmeyen kategori için `PERSONAL` default değeri
- Varsayılan field değerleri ve equality

**`TagCategoryTest`:**
- `fromString()` ile tüm kategorilerin (WORK, SCHOOL, HEALTH, SERVICES, EVENTS, PERSONAL) tanındığı
- Büyük/küçük harf duyarsız eşleşme
- `null` ve boş string için `PERSONAL` fallback değeri
- `TagCategory`'nin tam olarak 6 kategori içerdiği
- Tüm kategorilerin boş olmayan `displayName`'e sahip olduğu

---

### `SocialMediaPlatformAdapterTest` (10 test)
**Dosya:** `domain/scan_result/model/SocialMediaPlatformAdapterTest.kt`

JSON serileştirme/deserileştirme için özel `Gson` adapter'ını test eder. Kartvizitlerden taranan sosyal medya hesapları bu adapter ile JSON'a yazılıp okunur.

- `serialize()`'in platformu `platformName` string'ine (örn. `"LinkedIn"`) dönüştürdüğü
- `deserialize()`'in platform adından doğru enum değerini bulduğu
- Bilinmeyen platform adı için `OTHER` döndürüldüğü
- `null` JSON input için `OTHER` döndürüldüğü
- Facebook, Twitter, Instagram, LinkedIn gibi bilinen platformların round-trip tutarlılığı
- `SocialMediaPlatform`'un tam olarak 12 giriş içerdiği
- `OTHER` dışındaki tüm platformların serialize→deserialize round-trip'i

---

### `AppThemeTest` (8 test) ve `LanguageTest` (8 test)
**Dosyalar:** `domain/settings/model/AppThemeTest.kt`, `LanguageTest.kt`

Uygulama ayarlarının domain modellerini test eder.

**`AppThemeTest`:**
- `LIGHT`, `DARK`, `SYSTEM` değerlerinin varlığı
- Tam olarak 3 tema değerinin bulunduğu
- `valueOf()` ile string'den enum'a dönüşüm

**`LanguageTest`:**
- `fromCode("tr")`'nin `TURKISH`, `fromCode("en")`'nin `ENGLISH` döndürdüğü
- Bilinmeyen dil kodu için `ENGLISH` fallback
- Boş string için `ENGLISH` fallback
- Her dilin doğru `code` ve `displayName` değerlerine sahip olduğu
- `fromCode`'un büyük/küçük harf duyarlı olduğu (`"TR"` → `ENGLISH`)
- Tam olarak 2 dil seçeneği bulunduğu

---

## 4. DOMAIN Katmanı — Use Cases

Use case testlerinin tamamı **Mockito** ile yazılmıştır. Repository interface'leri mock'lanır, use case'in:
1. Parametreleri doğru repository metoduna iletip iletmediği
2. Repository'den gelen sonucu değiştirmeden döndürüp döndürmediği
3. Edge case'leri (boş liste, hata durumu vb.) doğru işleyip işlemediği test edilir.

---

### `AuthUseCasesTest` (11 test)
**Dosya:** `domain/auth/usecase/AuthUseCasesTest.kt`

Firebase kimlik doğrulama işlemlerini sarmalayan 7 use case'i test eder.

| Use Case | Test Senaryoları |
|---|---|
| `LoginUseCase` | Email/şifre ile giriş başarılı; geçersiz kredensiyellerde `Error` döndürme |
| `SignUpUseCase` | Yeni hesap oluşturma isteğinin repository'e iletilmesi |
| `SendForgotPasswordEmail` | Şifre sıfırlama emailinin doğru adrese gönderilmesi |
| `SendEmailVerification` | Email doğrulama isteğinin repository'e iletilmesi |
| `SignInWithGoogleUseCase` | Google ile giriş başarılı; başarısız durumda `Error` |
| `SignOutUseCase` | Oturumu kapatma isteğinin iletilmesi |
| `GetCurrentUserUseCase` | Oturum açık kullanıcının alınması; oturum yoksa `null` |

---

### `FindContactByMessageUseCaseTest` (7 test)
**Dosya:** `domain/chat/usecase/FindContactByMessageUseCaseTest.kt`

AI chat özelliğinin kalbini oluşturan use case'i test eder. Kullanıcının doğal dil ile yazdığı mesaja göre kişi araması yapar ve sonucu sealed class'a dönüştürür.

- Repository boş liste döndürünce → `AIContactResult.NoResult`
- Repository 1 kişi döndürünce → `AIContactResult.Single` (tek kişi bulundu)
- Repository 2+ kişi döndürünce → `AIContactResult.Multiple` (birden fazla eşleşme)
- Repository'nin doğru parametrelerle çağrıldığı
- `AIContactResult` sealed class alt sınıflarının doğru veri taşıdığı

---

### `FindDuplicateContactsUseCaseTest` (3 test)
**Dosya:** `domain/contact/usecase/FindDuplicateContactsUseCaseTest.kt`

Uzak sunucudaki kişiler ile telefon rehberindeki kişiler arasında mükerrerleri tespit eden use case'i test eder.

- Remote ve internal kişi listelerinin repository'e doğru iletildiği
- Mükerrer yokken boş liste döndürüldüğü
- Boş liste parametreleriyle güvenli çalışma

---

### `GetContactsListUseCaseTest` (3 test)
**Dosya:** `domain/contact/usecase/GetContactsListUseCaseTest.kt`

`ContentResolver` ile telefon rehberindeki kişileri ve değişiklikleri getiren use case'i test eder.

- `ContentResolver`'ın doğru şekilde repository'e iletildiği
- Kişi ve değişiklik çiftinin (`Pair`) doğru döndürüldüğü
- Boş rehber ile değişiklik olmayan senaryoda güvenli çalışma

---

### `GetRemoteContactsUseCaseTest` (4 test)
**Dosya:** `domain/contact/usecase/GetRemoteContactsUseCaseTest.kt`

Firebase'deki kişileri çeken use case'i test eder.

- Başarılı durumda kişi listesinin `Result.success` içinde döndürüldüğü
- Boş liste durumunun `Result.success(emptyList())` olarak döndürüldüğü
- Birden fazla kişinin doğru sayıda döndürüldüğü
- Ağ hatası durumunda `Result.failure` döndürüldüğü

---

### `MergeContactsUseCaseTest` (3 test)
**Dosya:** `domain/contact/usecase/MergeContactsUseCaseTest.kt`

Mükerrer kişileri tek kişide birleştiren use case'i test eder.

- Primary kişi, mükerrer listesi ve internal mükerrerlerinin repository'e iletildiği
- Birleştirme hatası durumunda `Result.failure` döndürüldüğü
- Boş mükerrer listesiyle güvenli çalışma

---

### `SuggestTagsForContactsUseCaseTest` (3 test)
**Dosya:** `domain/contact/usecase/SuggestTagsForContactsUseCaseTest.kt`

LLM kullanarak yeni kişilere otomatik etiket öneren use case'i test eder.

- Kişi listesinin repository'e iletildiği
- Boş liste için boş `ContactRequest` listesi döndürüldüğü
- Birden fazla kişi için birden fazla `ContactRequest` döndürüldüğü

---

### `CreateContactUseCaseTest` (3 test)
**Dosya:** `domain/scan/usecase/CreateContactUseCaseTest.kt`

Kartvizit taranan kişiyi Firebase'e kaydeden use case'i test eder.

- `ContactRequest`'in repository'e iletildiği
- Başarılı kayıtta `Result.success(Contact)` döndürüldüğü
- Kayıt hatasında `Result.failure` döndürüldüğü

---

### `ScanImageOnDeviceUseCaseTest` (3 test)
**Dosya:** `domain/scan/usecase/ScanImageOnDeviceUseCaseTest.kt`

Kartvizit görüntüsünü cihaz üzerinde (local LLM ile) işleyen use case'i test eder.

- `File` objesinin repository'e iletildiği
- Başarılı tarama sonucunda `Result.success(ScanResponse)` döndürüldüğü
- Tarama hatasında `Result.failure` döndürüldüğü

---

### `PhotoUseCasesTest` (5 test)
**Dosya:** `domain/photo/usecase/PhotoUseCasesTest.kt`

4 fotoğraf use case'ini test eder.

| Use Case | Test Senaryosu |
|---|---|
| `GetContactPhotoUseCase` | Kişi ID ve kullanıcı ID ile fotoğraf Flow'unun döndürüldüğü |
| `GetAllPhotosUseCase` | Kullanıcının tüm fotoğraflarının Flow olarak döndürüldüğü; boş liste durumu |
| `InsertPhotoUseCase` | Fotoğrafın repository `insertPhoto`'ya iletildiği |
| `DeletePhotoUseCase` | Fotoğrafın repository `deletePhoto`'ya iletildiği |

---

### `ThemeUseCasesTest` (6 test), `LanguageUseCasesTest` (4 test), `LlmUseCasesTest` (4 test)
**Dosyalar:** `domain/settings/usecase/ThemeUseCasesTest.kt`, `LanguageUseCasesTest.kt`, `LlmUseCasesTest.kt`

Uygulama ayarlarını okuyan ve yazan use case çiftlerini test eder (Get + Set pattern).

| Use Case Çifti | Test Senaryoları |
|---|---|
| `GetThemeUseCase` / `SetThemeUseCase` | LIGHT, DARK, SYSTEM temalarının Flow'dan okunması; repository'e yazılması |
| `GetLanguageUseCase` / `SaveLanguageUseCase` | TURKISH ve ENGLISH dillerinin Flow'dan okunması; repository'e kaydedilmesi |
| `GetUseLocalLlmUseCase` / `SetUseLocalLlmUseCase` | Local LLM kullanım tercihinin `true`/`false` olarak okunması ve yazılması |

---

### `HomeUseCasesTest` (3 test)
**Dosya:** `domain/home/usecase/HomeUseCasesTest.kt`

Ana sayfa için mevcut kullanıcı bilgisini getiren use case'i test eder.

- Oturum açık kullanıcının `ResponseState.Success(FirebaseUser)` olarak döndürüldüğü
- Oturum yoksa `ResponseState.Success(null)` döndürüldüğü
- Hata durumunda `ResponseState.Error` döndürüldüğü

---

## Kapsam Dışında Tutulan Alanlar

Aşağıdaki bileşenler Android runtime veya gerçek cihaz/emülatör gerektirdiğinden **instrumented test** kapsamına girer, unit test kapsamı dışındadır:

- **ViewModels** (`StateFlow`, `SharedFlow`, Android lifecycle bağımlılığı)
- **Room DAOs** (gerçek SQLite veritabanı gerektirir)
- **Firebase implementasyonları** (Firebase SDK bağlantısı gerektirir)
- **Hilt DI modülleri** (Android context gerektirir)
- **Composable UI bileşenleri** (Compose test framework gerektirir)
- **CameraX ve ML Kit** (kamera donanımı gerektirir)
