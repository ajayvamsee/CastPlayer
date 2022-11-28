package com.ajayvamsee.castplayer.settings

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceActivity
import com.ajayvamsee.castplayer.utils.Utils
import com.google.sample.cast.refplayer.R

/**
 * Created by Ajay Vamsee on 11/25/2022.
 * Time : 16:02 HRS
 */
class CastPreference : PreferenceActivity(), OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.application_preference)

        val versionPref = findPreference("app_version") as EditTextPreference
        versionPref.title = getString(R.string.version, Utils.getAppVersionName(context = this))
    }
}