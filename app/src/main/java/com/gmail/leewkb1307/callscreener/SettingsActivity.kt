package com.gmail.leewkb1307.callscreener

import android.app.Activity
import android.app.role.RoleManager
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(onModeChange)
    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(onModeChange)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    private fun requestScreenerRole() {
        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        requestRoleLauncher.launch(intent)
    }

    private val requestRoleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            Toast.makeText(this,"Call screener setup not good!", Toast.LENGTH_SHORT).show()
            resetPrefMode()
            finish()
        }
    }

    private val onModeChange = SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
        if (key == "prefMode") {
            val modeVal = sharedPref.getString(key, "allow_all")
            if (modeVal != "allow_all") {
                requestScreenerRole()
            }
        }
    }

    private fun resetPrefMode() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()?.putString("prefMode", "allow_all")?.apply()
    }
}
