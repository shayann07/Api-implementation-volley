package com.shayan.firstapi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.shayan.firstapi.databinding.ActivityMainSignUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivitySignUp : AppCompatActivity() {

    private lateinit var binding: ActivityMainSignUpBinding
    private val apiUrl = "https://cricdex.enfotrix.com/api/register"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            signupButton.setOnClickListener {
                val firstName = firstName.text.toString()
                val lastName = lastName.text.toString()
                val phone = phoneEditText.text.toString()
                val pass = passwordEditText.text.toString()
                val cPass = confirmPasswordEditText.text.toString()

                if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || pass.isEmpty() || cPass.isEmpty())
                {
                    Toast.makeText(this@MainActivitySignUp, "All fields are required!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (pass == cPass) {
                    // ModelUser object
                    val modelUser = ModelUser(firstName, lastName, phone, pass, cPass)

                    // co-routines of kotlin
                    lifecycleScope.launch {
                        registerUser(modelUser)
                    }
                } else {

                    Toast.makeText(
                        this@MainActivitySignUp,
                        "Passwords do not match!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private suspend fun registerUser(user: ModelUser) {
        // network request in a background
        withContext(Dispatchers.IO) {

            try {
                // JSON body to send to api
                val jsonBody = JSONObject().apply {
                    put("first_name", user.firstName)
                    put("last_name", user.lastName) // Corrected the property name
                    put("phone_number", user.phone)
                    put("password", user.password)
                    put("c_password", user.cPass)
                }

                //Volley request queue
                val requestQueue = Volley.newRequestQueue(this@MainActivitySignUp)

                // Create the JSON Object request
                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST, apiUrl, jsonBody,
                    Response.Listener { response ->

                        Toast.makeText(
                            this@MainActivitySignUp,
                            "Successfully Created Account",
                            Toast.LENGTH_SHORT
                        ).show()


                        val intent = Intent(this@MainActivitySignUp, MainActivityLogin::class.java)
                        startActivity(intent)
                    },
                    Response.ErrorListener { error ->

                        Toast.makeText(
                            this@MainActivitySignUp,
                            "Error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    // kuch samjh ni ayi header ki
                    override fun getHeaders(): Map<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        return headers
                    }
                }


                requestQueue.add(jsonObjectRequest)

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivitySignUp,
                        "Error occurred: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
