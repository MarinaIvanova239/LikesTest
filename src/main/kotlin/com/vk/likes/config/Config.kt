package com.vk.likes.config

object Config {
    val accessTokenWithWallScope = System.getProperty("vk.accessToken.wallScope", "")
    val accessTokenWithOfflineScope = System.getProperty("vk.accessToken.offlineScope", "")
    val clientId = System.getProperty("vk.clientId", "").toInt()
}