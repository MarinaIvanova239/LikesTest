package com.vk.likes.entities.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.vk.likes.entities.ObjectType

data class AddLike(
        @get:JsonProperty("type") val objectType: ObjectType = ObjectType.audio,
        @get:JsonProperty("owner_id") val ownerId: Int = 0,
        @get:JsonProperty("item_id") val objectId: Int = 0,
        @get:JsonProperty("access_key") val accessKey: Int = 0
)

data class GetLikes(
        @get:JsonProperty("type") val objectType: ObjectType = ObjectType.audio,
        @get:JsonProperty("owner_id") val ownerId: Int = 0,
        @get:JsonProperty("item_id") val objectId: Int = 0,
        @get:JsonProperty("page_url") val pageUrl: String = "",
        @get:JsonProperty("filter") val filter: String = "",
        @get:JsonProperty("friends_only") val friendsOnly: Int = 0,
        @get:JsonProperty("extended") val extended: Int = 0,
        @get:JsonProperty("offset") val offset: Int = 0,
        @get:JsonProperty("count") val count: Int = 0,
        @get:JsonProperty("skip_own") val skipOwn: Int = 0
)