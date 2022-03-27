package com.vk.likes.services

object AuthService {

    fun getAccessToken(): String {
        return System.getProperty("accessToken")
    }
}