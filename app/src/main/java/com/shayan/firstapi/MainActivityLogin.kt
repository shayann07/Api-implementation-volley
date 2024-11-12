package com.shayan.firstapi

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shayan.firstapi.databinding.ActivityMainLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivityLogin : AppCompatActivity() {

    private lateinit var binding: ActivityMainLoginBinding
    private val apiUrl = "https://cricdex.enfotrix.com/api/login"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("PrefsDatabase", MODE_PRIVATE)

        binding.signinButton.setOnClickListener {
            val enteredPhone = binding.phoneEditText.text.toString()
            val enteredPass = binding.passwordEditText.text.toString()

            if (enteredPhone.isNotEmpty() && enteredPass.isNotEmpty()) {
                lifecycleScope.launch {
                    loginUser(enteredPhone, enteredPass)
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupScreen.setOnClickListener {
            startActivity(Intent(this, MainActivitySignUp::class.java))
        }
    }

    private suspend fun loginUser(phone: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val jsonBody = JSONObject().apply {
                    put("phone_number", phone)
                    put("password", password)
                }

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST, apiUrl, jsonBody,
                    Response.Listener { response ->
                        if (response.optBoolean("success", false) && response.has("data")) {
                            val token = response.getJSONObject("data").optString("token", "")
                            saveToken(token)
                            Toast.makeText(this@MainActivityLogin, "Successful Login", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@MainActivityLogin, MainActivityHome::class.java))
                            finish()
                        } else {
                            val message = response.optString("message", "Login failed.")
                            Toast.makeText(this@MainActivityLogin, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this@MainActivityLogin, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    override fun getHeaders() = mapOf("Content-Type" to "application/json")
                }

                Volley.newRequestQueue(this@MainActivityLogin).add(jsonObjectRequest)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivityLogin, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString("user_token", token).apply()
    }
}
