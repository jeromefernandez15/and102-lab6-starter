
package com.codepath.lab6

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.serialization.json.Json
import android.content.res.Configuration


fun createJson() = Json {
    isLenient = true
    ignoreUnknownKeys = true
    useAlternativeNames = false
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val prefs = getSharedPreferences("campground_settings", MODE_PRIVATE)

        // Restore dark mode preference.
        val darkMode = prefs.getBoolean("dark_mode", false)

        val desiredMode = if (darkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        if (AppCompatDelegate.getDefaultNightMode() != desiredMode) {
            AppCompatDelegate.setDefaultNightMode(desiredMode)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Restore keep-screen-awake preference.
        if (prefs.getBoolean("keep_awake", false)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        val bottomNavigation =
            findViewById<BottomNavigationView>(
                R.id.bottom_navigation
            )

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.navigation_parks -> {
                    replaceFragment(ParksFragment())
                    true
                }

                R.id.navigation_campgrounds -> {
                    replaceFragment(CampgroundFragment())
                    true
                }

                R.id.navigation_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }


        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId =
                R.id.navigation_home
        }

    }


    override fun onConfigurationChanged(
        newConfig: Configuration
    ) {
        super.onConfigurationChanged(newConfig)

        // Keep the current fragment and its data
        // instead of recreating the Activity.
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame_layout, fragment)
            .commit()
    }
}
