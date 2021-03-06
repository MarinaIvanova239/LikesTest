package com.vk.likes.entities.request

import com.vk.likes.entities.FilterType
import com.vk.likes.entities.FilterType.likes
import com.vk.likes.entities.ObjectType
import com.vk.likes.entities.ObjectType.post

data class AddLikeParams(
        val objectType: ObjectType = post,
        val ownerId: Int = 0,
        val objectId: Int = 0,
        val accessKey: String? = null
)

data class CheckLikeParams(
        val objectType: ObjectType = post,
        val ownerId: Int = 0,
        val objectId: Int = 0,
        val userId: Int = 0
)

data class GetLikesParams(
        val objectType: ObjectType = post,
        val ownerId: Int = 0,
        val objectId: Int = 0,
        val pageUrl: String? = null,
        val filter: FilterType = likes,
        val friendsOnly: Int? = null,
        val extended: Int? = null,
        val offset: Int? = null,
        val count: Int? = null,
        val skipOwn: Int = 0
)