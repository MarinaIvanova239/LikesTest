package com.vk.likes.entities.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LikesCount(
        @get:JsonProperty("likes") val likesCount: Long = 0
)

data class ObjectIsLiked(
        @get:JsonProperty("liked") val isLiked: Int = 0,
        @get:JsonProperty("copied") val isCopied: Int = 0
)

data class UserLikesList(
        @get:JsonProperty("count") val userCount: Long = 0,
        @get:JsonProperty("items") val userIds: List<Long> = emptyList()
)

