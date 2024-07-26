package com.example.loginapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginapp.api.Login
import com.example.loginapp.api.LoginService
import com.example.loginapp.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var usernameInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button
    lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        messageTextView = findViewById(R.id.message_text_view)

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            Log.i("Test Credentials", "Username: $username, Password: $password")

            // 로그인 요청 전송
            val loginService = RetrofitClient.instance.create(LoginService::class.java)
            val call = loginService.login(username, password)

            // 비동기 호출
            call.enqueue(object : Callback<Login> {
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        loginResponse?.let {
                            handleLoginResponse(it)
                        } ?: run {
                            Log.e("Login Error", "Response body is null")
                            showMessage("Unexpected error occurred")
                        }
                    } else {
                        Log.e("Login Error", "Login failed with response code ${response.code()}")
                        showMessage("Login failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.e("Login Error", "Login request failed", t)
                    showMessage("Network error: ${t.message}")
                }
            })
        }
    }

    private fun handleLoginResponse(loginResponse: Login) {
        val message = if (loginResponse.result) {
            "Login successful: ${loginResponse.message}"
        } else {
            "Login failed: ${loginResponse.message}"
        }
        showMessage(message)
    }

    private fun showMessage(message: String) {
        messageTextView.text = message
        messageTextView.visibility = TextView.VISIBLE
    }
}