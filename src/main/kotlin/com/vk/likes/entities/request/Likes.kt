package com.vk.likes.entities.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.vk.likes.entities.ObjectType

data class AddLike(
        @get:JsonProperty("number_of_instances") val objectType: ObjectType = ObjectType.audio,
        @get:JsonProperty("owner_id") val ownerId: Int = 0,
        @get:JsonProperty("item_id") val objectId: Int = 0,
        @get:JsonProperty("access_key") val accessKey: Int = 0
)