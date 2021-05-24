package com.ahmer.accounting.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.ahmer.accounting.R
import com.ahmer.accounting.databinding.SettingsBinding
import com.ahmer.accounting.helper.Constants
import com.ahmer.accounting.helper.MyAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.guardanis.applock.AppLock
import com.guardanis.applock.dialogs.LockCreationDialogBuilder
import io.ahmer.utils.HelperUtils
import io.ahmer.utils.utilcode.*
import java.io.File
import java.util.*

class Settings : AppCompatActivity() {

    private lateinit var mBinding: SettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.toolbarSettings.overflowIcon?.setTint(Color.WHITE)
        }
        if (NetworkUtils.isConnected()) {
            MyAds.loadInterstitialAd(this)
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

            val lockScreenPref: SPUtils = SPUtils.getInstance(Constants.PREFERENCE_LOCKSCREEN)
            val btnLockScreen: SwitchPreferenceCompat = findPreference("lockScreen")!!
            btnLockScreen.setOnPreferenceChangeListener { preference, newValue ->
                val isChecked = newValue as Boolean
                if (isChecked) {
                    LockCreationDialogBuilder(requireActivity())
                        .onCanceled {
                            ToastUtils.showShort("You canceled...")
                        }
                        .onLockCreated {
                            ToastUtils.showShort("Lock created!")
                        }
                        .show()
                    lockScreenPref.put(Constants.PREFERENCE_LOCKSCREEN_KEY, isChecked)
                } else {
                    AppLock.getInstance(context).invalidateEnrollments()
                    lockScreenPref.put(Constants.PREFERENCE_LOCKSCREEN_KEY, isChecked)
                }
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