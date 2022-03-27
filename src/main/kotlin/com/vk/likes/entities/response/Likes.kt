package com.vk.likes.entities.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LikesCount(
        @JsonProperty("likes") val likesCount: Long = 0
)

data class ObjectIsLiked(
        @JsonProperty("liked") val isLiked: Int = 0,
        @JsonProperty("copied") val isCopied: Int = 0
)

data class UserLikesList(
        @JsonProperty("count") val userCount: Long = 0,
        @JsonProperty("items") val userIds: List<Long> = emptyList()
)

