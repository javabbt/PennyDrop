package dev.mfazio.pennydrop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs =
            PreferenceManager.getDefaultSharedPreferences(this)

        val themeId = when (prefs.getString("theme", "AppTheme")) {
            "Crew" -> R.style.Crew
            "FTD" -> R.style.FTD
            "GPG" -> R.style.GPG
            "Hazel" -> R.style.Hazel
            "Kotlin" -> R.style.Kotlin
            else -> R.style.AppTheme
        }

        setTheme(themeId)

        val nightMode = when (prefs.getString("themeMode", "")) {
            "Light" -> AppCompatDelegate.MODE_NIGHT_NO
            "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(nightMode)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.containerFragment) as NavHostFragment
        this.navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(this.navController)

        val appBarConfiguration = AppBarConfiguration(bottomNav.menu)
        setupActionBarWithNavController(this.navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.options, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        (this::navController.isInitialized && item.onNavDestinationSelected(this.navController)) ||
            super.onOptionsItemSelected(item)

    override fun onSupportNavigateUp(): Boolean =
        (this::navController.isInitialized && this.navController.navigateUp()) ||
            super.onSupportNavigateUp()
}
