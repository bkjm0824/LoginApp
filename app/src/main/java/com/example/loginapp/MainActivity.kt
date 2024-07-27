package com.example.loginapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.loginapp.api.Data
import com.example.loginapp.api.Login
import com.example.loginapp.api.LoginService
import com.example.loginapp.api.RetrofitClient
import com.example.loginapp.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 요소 초기화
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        messageTextView = findViewById(R.id.message_text_view)

        // 로그인 버튼 클릭 리스너 설정
        loginBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            Log.i("Test Credentials", "Email: $email, Password: $password")

            // 로그인 요청 전송
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        val user = User(email, password) // User 객체 생성
        val loginService = RetrofitClient.instance.create(LoginService::class.java)
        val call = loginService.login(user) // User 객체 전달

        // 비동기 네트워크 요청
        call.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        handleLoginResponse(loginResponse)
                    } else {
                        Log.e("Login Error", "Response body is null")
                        showMessage("Unexpected error occurred")
                    }
                } else {
                    Log.e("Login Error", "Login failed with response code ${response.code()}. Message: ${response.message()}")
                    showMessage("Login failed: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                Log.e("Login Error", "Login request failed", t)
                showMessage("Network error: ${t.message}")
            }
        })
    }

    private fun handleLoginResponse(loginResponse: Login) {
        val message = if (loginResponse.result) {
            "Login successful: ${loginResponse.message}. Token: ${loginResponse.data.token}, ExprTime: ${loginResponse.data.exprTime}"
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