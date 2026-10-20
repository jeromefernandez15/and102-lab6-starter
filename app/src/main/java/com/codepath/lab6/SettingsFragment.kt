
package com.codepath.lab6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val prefs = requireContext().getSharedPreferences(
            "campground_settings",
            0
        )

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 20)
        }

        val title = TextView(requireContext()).apply {
            text = "Settings"
            textSize = 26f
            setPadding(0, 0, 0, 32)
        }

        val darkMode = SwitchCompat(requireContext()).apply {
            text = "Dark Mode"
            textSize = 18f
            isChecked = prefs.getBoolean("dark_mode", false)

            setOnCheckedChangeListener { _, enabled ->
                prefs.edit()
                    .putBoolean("dark_mode", enabled)
                    .apply()

                AppCompatDelegate.setDefaultNightMode(
                    if (enabled)
                        AppCompatDelegate.MODE_NIGHT_YES
                    else
                        AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        val keepAwake = SwitchCompat(requireContext()).apply {
            text = "Keep Screen Awake"
            textSize = 18f
            isChecked = prefs.getBoolean("keep_awake", false)
            setPadding(0, 30, 0, 0)

            setOnCheckedChangeListener { _, enabled ->
                prefs.edit()
                    .putBoolean("keep_awake", enabled)
                    .apply()

                if (enabled) {
                    requireActivity().window.addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    )
                } else {
                    requireActivity().window.clearFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    )
                }
            }
        }

        layout.addView(title)
        layout.addView(darkMode)
        layout.addView(keepAwake)

        return layout
    }
}
