package com.shayan.firstapi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.shayan.firstapi.databinding.ActivityMainHomeBinding
import kotlin.reflect.KMutableProperty0

class MainActivityHome : AppCompatActivity() {

    private lateinit var binding: ActivityMainHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var isArrowDownICloud = true
    private var isArrowDownOutlook = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("PrefsDatabase", MODE_PRIVATE)

        // Set up menu for log-out functionality
        binding.menuImageView.setOnClickListener { showPopupMenu() }

        // Toggle visibility for containers on click
        binding.textviewICloud.setOnClickListener {
            toggleVisibility(binding.iCloudContainer, ::isArrowDownICloud)
        }
        binding.textviewOutlook.setOnClickListener {
            toggleVisibility(binding.outlookContainer, ::isArrowDownOutlook)
        }
    }

    private fun showPopupMenu() {
        PopupMenu(this, binding.menuImageView).apply {
            menuInflater.inflate(R.menu.menu_dropdown_toolbar, menu)
            setOnMenuItemClickListener { handleMenuItemClick(it) }
            show()
        }
    }

    private fun handleMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out -> {
                sharedPreferences.edit().clear().apply()
                startActivity(Intent(this, MainActivityLogin::class.java))
                finish()
                true
            }
            else -> false
        }
    }

    private fun toggleVisibility(container: LinearLayout, arrowState: KMutableProperty0<Boolean>) {
        container.visibility = if (arrowState.get()) View.GONE else View.VISIBLE
        arrowState.set(!arrowState.get())
    }
}
