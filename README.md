## Card Connect

Auth-first Android app that signs users in with email/password or Google, then routes them into a simple scan/home surface. Built with Jetpack Compose, Hilt, Navigation, and Firebase Auth.

### What is here
- Compose UI for sign-in, password reset, and a placeholder scan screen.
- Firebase email/password login with automatic registration fallback.
- Google Sign-In via Credential Manager.
- Hilt DI for auth + platform services.
- Kotlin coroutines for async flows.

### Quick start
1) Prerequisites: Android Studio Koala+ (AGP 8.5), JDK 17, Android SDK 34 installed.
2) Add your `google-services.json` to `app/`.
3) Update `app/src/main/res/values/strings.xml` with your `default_web_client_id` if it differs.
4) Build and run: `./gradlew :app:assembleDebug` or launch from Android Studio.

### Notes on networking
- Release build blocks cleartext traffic by default.
- Debug build allows cleartext to `192.168.1.103` only (see `app/src/debug/res/xml/network_security_config.xml`).

### Testing
- `./gradlew test` for unit tests.
- `./gradlew connectedAndroidTest` for instrumentation (requires device/emulator).

### Roadmap ideas
- Add UI tests for auth flows.
- Replace hard-coded strings with i18n where needed.
- Wire real scan feature and contact/profile tabs.
