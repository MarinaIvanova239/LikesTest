package com.vk.likes.entities.response

import com.fasterxml.jackson.annotation.JsonProperty

data class CurrentLikes(
        @get:JsonProperty("likes") val likes: Long = 0
)

