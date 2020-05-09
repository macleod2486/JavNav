/*
*
*   JavNav 
*    a simple application for general use of the Texas A&M-Kingsville Campus. 
*    
*    Copyright (C) 2019  Manuel Gonzales Jr.
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see [http://www.gnu.org/licenses/].
*
*/
//This is where all the settings appear.
package com.senior.javnav

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.preference.*

class Preferences : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        PreferenceManager.setDefaultValues(requireContext(), R.xml.settings, false)
        initSummary(preferenceScreen)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        updatePrefSummary(findPreference(key))
    }

    private fun initSummary(p: Preference) {
        if (p is PreferenceGroup) {
            val pGrp = p
            for (i in 0 until pGrp.preferenceCount) {
                initSummary(pGrp.getPreference(i))
            }
        } else {
            updatePrefSummary(p)
        }
    }

    private fun updatePrefSummary(p: Preference?) {

        if(p is CheckBoxPreference)
        {
            if(p.key == "secScreen")
            {
                val shared = PreferenceManager.getDefaultSharedPreferences(requireContext())
                if (shared.getBoolean("secScreen", false)) {
                    requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                    Log.i("Preferences", "Enable secure screen")
                } else {
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    Log.i("Preferences", "Disable secure screen")
                }
            }
        }

        if (p is ListPreference) {
            p.setSummary(p.entry)
        }
        if (p is EditTextPreference) {
            if (p.getTitle().toString().toLowerCase().contains("password")) {
                p.setSummary("******")
            } else {
                p.setSummary(p.text)
            }
        }
        if (p is MultiSelectListPreference) {
            val shared = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val selections = shared.getStringSet(p.key, null)

            if (selections != null) {
                var summary = ""
                selections.forEach {
                    Log.i("Preference", it);
                    summary += when (it) {
                        "t" -> " Tolls "
                        "f" -> " Ferries "
                        "h" -> " Highways "
                        else -> it
                    }
                }

                p.setSummary(summary)
            }
        }
    }
}