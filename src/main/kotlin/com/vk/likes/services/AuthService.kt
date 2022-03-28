package com.vk.likes.services

import com.vk.likes.config.Config

object AuthService {

    fun getOfflineScopeAccessToken(): String {
        return Config.accessTokenWithOfflineScope
    }

    fun getWallScopeAccessToken(): String {
        return Config.accessTokenWithWallScope
    }
}
