package com.example.loginapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("auth/user/login")
    fun login(@Body user: User): Call<Login>
}