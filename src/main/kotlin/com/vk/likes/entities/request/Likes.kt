package com.vk.likes.entities.request

import com.vk.likes.entities.ObjectType

data class AddLikeParams(
        val objectType: ObjectType = ObjectType.audio,
        val ownerId: Int = 0,
        val objectId: Int = 0,
        val accessKey: String? = null
)

data class CheckLikeParams(
        val objectType: ObjectType = ObjectType.audio,
        val ownerId: Int = 0,
        val objectId: Int = 0,
        val userId: Int = 0
)

data class GetLikesParams(
        val objectType: ObjectType = ObjectType.audio,
        val ownerId: Int = 0,
        val objectId: Int = 0,
        val pageUrl: String = "",
        val filter: String = "likes",
        val friendsOnly: Int = 0,
        val extended: Int = 0,
        val offset: Int = 0,
        val count: Int = 0,
        val skipOwn: Int = 0
)