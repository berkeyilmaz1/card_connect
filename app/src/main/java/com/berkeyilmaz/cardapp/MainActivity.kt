package com.berkeyilmaz.cardapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.core.cache.languageDataStore
import com.berkeyilmaz.cardapp.core.navigation.AppNavHost
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.domain.settings.model.Language
import com.berkeyilmaz.cardapp.presentation.settings.viewmodel.SettingsViewModel
import com.berkeyilmaz.cardapp.presentation.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var currentLanguageCode: String? = null

    override fun attachBaseContext(newBase: Context) {
        // Kayıtlı dil ayarını uygula - Hilt henüz inject etmediği için doğrudan DataStore'dan okuyoruz
        val localizedContext = runBlocking {
            try {
                val languageKey = stringPreferencesKey("language_code")
                val languageCode = newBase.languageDataStore.data.map { preferences ->
                    preferences[languageKey] ?: Language.ENGLISH.code
                }.first()

                currentLanguageCode = languageCode
                val language = Language.fromCode(languageCode)
                Log.d("BerkeTAG", "attachBaseContext: language = ${language.code}")
                applyLanguage(newBase, language)
            } catch (e: Exception) {
                Log.e("BerkeTAG", "attachBaseContext error", e)
                newBase
            }
        }
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplashScreen = true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val currentTheme by viewModel.currentTheme.collectAsState()
            val currentLanguage by viewModel.currentLanguage.collectAsState()
            val firebaseUser = FirebaseAuth.getInstance().currentUser

            // Dil değişikliğini dinle ve activity'yi yeniden başlat
            LaunchedEffect(currentLanguage.code) {
                Log.d(
                    "BerkeTAG",
                    "LaunchedEffect triggered: currentLanguageCode=$currentLanguageCode, new=${currentLanguage.code}"
                )
                // İlk yüklemede currentLanguageCode null olabilir, bu durumda set ediyoruz
                if (currentLanguageCode == null) {
                    Log.d("BerkeTAG", "First language setup: ${currentLanguage.code}")
                    currentLanguageCode = currentLanguage.code
                } else if (currentLanguageCode != currentLanguage.code) {
                    Log.d(
                        "BerkeTAG",
                        "Language changed from $currentLanguageCode to ${currentLanguage.code}, recreating activity..."
                    )
                    recreate()
                }
            }

            LaunchedEffect(Unit) {
                keepSplashScreen = false
            }

            val startDestination = if (firebaseUser != null) {
                Screen.Main.Graph.route
            } else {
                Screen.Auth.Graph.route
            }

            AppTheme(theme = currentTheme) {
                AppNavHost(startDestination = startDestination)
            }
        }
    }

    private fun applyLanguage(context: Context, language: Language): Context {
        val locale = Locale.forLanguageTag(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
