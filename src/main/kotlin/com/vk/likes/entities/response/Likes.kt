package com.vk.likes.entities.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LikesCountRs(
        @get:JsonProperty("response") val response: LikesCount = LikesCount()
)

data class LikesCount(
        @get:JsonProperty("likes") val likesCount: Long = 0
)

data class ObjectIsLikedRs(
        @get:JsonProperty("response") val response: ObjectIsLiked = ObjectIsLiked()
)

data class ObjectIsLiked(
        @get:JsonProperty("liked") val isLiked: Long = 0,
        @get:JsonProperty("copied") val isCopied: Long = 0
)

data class UserLikesListRs(
        @get:JsonProperty("response") val response: UserLikesList = UserLikesList()
)

data class UserLikesList(
        @get:JsonProperty("count") val userCount: Long = 0,
        @get:JsonProperty("items") val userIds: List<Long> = emptyList()
)

