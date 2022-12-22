package com.gmail.leewkb1307.callscreener

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            loadScreen()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        verifyScreenerRole()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.registerOnSharedPreferenceChangeListener(onModeChange)
    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.unregisterOnSharedPreferenceChangeListener(onModeChange)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    private fun verifyScreenerRole() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val modeVal = sharedPref.getString("prefMode", null)
        if (modeVal != "allow_all") {
            requestScreenerRole()
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
            loadScreen()
        } else {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            val modeVal = sharedPref.getString("prefMode", null)
            if (modeVal == "allow_contact") {
                requestReadContacts()
            }
        }
    }

    private val onModeChange = SharedPreferences.OnSharedPreferenceChangeListener { sharedPref, key ->
        if (key == "prefMode") {
            verifyScreenerRole()
        }
    }

    private fun resetPrefMode() {
        PreferenceManager.getDefaultSharedPreferences(this).edit()?.putString("prefMode", "allow_all")?.apply()
    }

    private fun loadScreen() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    private fun requestReadContacts() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) -> {
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this,"Unable to read contacts!", Toast.LENGTH_SHORT).show()
                resetPrefMode()
                loadScreen()
            }
        }
}
