package com.example.tenantmanagementsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tenantmanagementsystem.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // The most recently saved tenant. Lost on rotation unless we save it
    private var lastTenant: Tenant? = null

    // How many times SAVE has been tapped successfully (saved/restored with putInt/getInt)
    private var saveCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Write a message to Logcat every time onCreate() runs.
        // "TENANT_LIFECYCLE" is a tag: a label we search for in Logcat.
        Log.d("TENANT_LIFECYCLE", "MainActivity: onCreate")

        // Create the views from activity_main.xml and show them
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ── RESTORE STATE (only after a rotation) ─────────────────
        // savedInstanceState is null the FIRST time the Activity opens.
        if (savedInstanceState != null) {
            // Read each value back using the SAME keys we saved with
            val name = savedInstanceState.getString("tenant_name")
            val phone = savedInstanceState.getString("tenant_phone")
            val rent = savedInstanceState.getString("tenant_rent")

            // Read the counter back; 0 is the default if nothing was saved
            saveCount = savedInstanceState.getInt("save_count", 0)
            Toast.makeText(this, "SAVE was tapped $saveCount time(s) before rotating", Toast.LENGTH_SHORT).show()

            // Only rebuild the tenant if all three were saved
            if (name != null && phone != null && rent != null) {
                val tenant = Tenant(name, phone, rent)
                lastTenant = tenant      // so CALL TENANT works again
                binding.tenant = tenant  // so the summary appears again (Data Binding)
            }
        }

        // Welcome Toast (only on a fresh open, not after every rotation)
        val email = intent.getStringExtra("EMAIL")
        if (email != null && savedInstanceState == null) {
            Toast.makeText(this, "Logged in as $email", Toast.LENGTH_SHORT).show()
        }

        // ── SAVE button ───────────────────────────────────────────
        binding.saveButton.setOnClickListener {
            val name = binding.tenantNameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val rent = binding.rentEditText.text.toString().trim()

            if (name.isEmpty()) {
                binding.tenantNameEditText.error = "Required"
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                binding.phoneEditText.error = "Required"
                return@setOnClickListener
            }

            if (rent.isEmpty()) {
                binding.rentEditText.error = "Required"
                return@setOnClickListener
            }

            val tenant = Tenant(name, phone, rent)
            binding.tenant = tenant   // Data Binding shows the summary
            lastTenant = tenant       // remember it for CALL / SHARE
            saveCount++               // count this successful SAVE
        }

        // ── CALL TENANT button (implicit Intent) ──────────────────
        binding.callButton.setOnClickListener {
            val tenant = lastTenant

            if (tenant == null) {
                Toast.makeText(this, "Save a tenant first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tenant.phone}"))
            startActivity(intent)
        }

        // ── SHARE button (implicit Intent) ────────────────────────
        binding.shareButton.setOnClickListener {
            val tenant = lastTenant

            if (tenant == null) {
                Toast.makeText(this, "Save a tenant first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, tenant.summary())
            startActivity(Intent.createChooser(intent, "Share tenant"))
        }
    }

    // ── SAVE STATE ────────────────────────────────────────────────
    // Android calls this just before destroying the Activity
    // (for example, when the phone rotates).
    // outState is the Bundle that Android keeps for us.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("TENANT_LIFECYCLE", "MainActivity: onSaveInstanceState")

        // Save the counter (an Int) under its own key
        outState.putInt("save_count", saveCount)

        // Copy into a val so Kotlin knows it can't change after the check
        val tenant = lastTenant
        if (tenant != null) {
            // Store each field under its own KEY.
            // onCreate() reads them back with the SAME keys.
            outState.putString("tenant_name", tenant.name)
            outState.putString("tenant_phone", tenant.phone)
            outState.putString("tenant_rent", tenant.rent)
        }
    }

    // ── LIFECYCLE METHODS ─────────────────────────────────────────
    // Android calls each of these automatically. We only add a log
    // line so we can watch the order in Logcat.

    // The screen is now VISIBLE
    override fun onStart() {
        super.onStart()
        Log.d("TENANT_LIFECYCLE", "MainActivity: onStart")
    }

    // The screen is in FRONT and the user can interact with it
    override fun onResume() {
        super.onResume()
        Log.d("TENANT_LIFECYCLE", "MainActivity: onResume")
    }

    // The screen is LEAVING the front (e.g. Home was pressed)
    override fun onPause() {
        super.onPause()
        Log.d("TENANT_LIFECYCLE", "MainActivity: onPause")
    }

    // The screen is NO LONGER VISIBLE
    override fun onStop() {
        super.onStop()
        Log.d("TENANT_LIFECYCLE", "MainActivity: onStop")
    }

    // The user came BACK to a stopped screen
    override fun onRestart() {
        super.onRestart()
        Log.d("TENANT_LIFECYCLE", "MainActivity: onRestart")
    }

    // The screen is being DESTROYED
    override fun onDestroy() {
        super.onDestroy()
        Log.d("TENANT_LIFECYCLE", "MainActivity: onDestroy")
    }
}
