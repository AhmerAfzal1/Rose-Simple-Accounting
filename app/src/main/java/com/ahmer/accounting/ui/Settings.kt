package com.ahmer.accounting.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.ahmer.accounting.R
import com.ahmer.accounting.helper.Constants
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.ahmer.utils.HelperUtils
import io.ahmer.utils.utilcode.AppUtils
import io.ahmer.utils.utilcode.CleanUtils
import io.ahmer.utils.utilcode.SPUtils
import io.ahmer.utils.utilcode.Utils
import java.io.File
import java.util.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.overflowIcon?.setTint(Color.WHITE)
        }
        toolbar.setOnClickListener {
            finish()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private lateinit var btnCaches: Preference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

            val prefTheme: SPUtils = SPUtils.getInstance(Constants.PREFERENCE_THEME)
            val btnTheme: SwitchPreferenceCompat = findPreference("darkTheme")!!
            if (btnTheme.isChecked) {
                btnTheme.title = getString(R.string.title_light_mode)
                btnTheme.summary = getString(R.string.summary_light_mode)
            } else {
                btnTheme.title = getString(R.string.title_dark_mode)
                btnTheme.summary = getString(R.string.summary_dark_mode)
            }
            btnTheme.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference: Preference?, newValue: Any ->
                    val isChecked = newValue as Boolean
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    prefTheme.put(Constants.PREFERENCE_THEME_KEY, isChecked)
                    true
                }

            btnCaches = findPreference("clearCaches")!!
            btnCaches.setOnPreferenceClickListener {
                CleanUtils.cleanExternalCache()
                CleanUtils.cleanInternalCache()
                CleanUtils.cleanInternalFiles()
                initializeCache()
                return@setOnPreferenceClickListener true
            }

            val versionApp: Preference = findPreference("appVersion")!!
            versionApp.summary = String.format(
                Locale.getDefault(),
                getString(
                    R.string.summary_version,
                    AppUtils.getAppVersionName(),
                    AppUtils.getAppVersionCode()
                )
            )
            initializeCache()
        }

        override fun setDivider(divider: Drawable?) {
            super.setDivider(null)
        }

        override fun setDividerHeight(height: Int) {
            super.setDividerHeight(0)
        }

        private fun initializeCache() {
            var size = 0L
            size += getDirSize(Utils.getApp().cacheDir)
            size += getDirSize(Utils.getApp().externalCacheDir!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                size += getDirSize(Utils.getApp().codeCacheDir)
            }
            btnCaches.summary =
                getString(R.string.summary_cache_size, HelperUtils.getFileSize(size))
        }

        private fun getDirSize(dir: File): Long {
            var size = 0L
            for (file in dir.listFiles()!!) {
                if (file != null && file.isDirectory) {
                    size += getDirSize(file)
                } else if (file != null && file.isFile) {
                    size += file.length()
                }
            }
            return size
        }
    }
}