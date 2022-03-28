package com.vk.likes.entities.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorDescription(
        @JsonProperty("error_code") val code: Int = 0,
        @JsonProperty("error_msg") val message: String = "",
        @JsonProperty("request_params") val requestParameters: List<Any> = emptyList()
)