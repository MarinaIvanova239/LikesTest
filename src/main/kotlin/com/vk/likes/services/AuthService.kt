package com.vk.likes.services

import com.vk.likes.config.Properties

object AuthService {
    // here can be authorization flow, but let's assume that current implementations is getting token from system properties

    fun getOfflineScopeAccessToken(): String {
        return Properties.accessTokenWithOfflineScope
    }

    fun getWallScopeAccessToken(): String {
        return Properties.accessTokenWithWallScope
    }
}
