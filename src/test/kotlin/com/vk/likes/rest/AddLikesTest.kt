package com.vk.likes.rest

import com.vk.likes.base.BaseTest
import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.services.RestService.addLike
import org.hamcrest.MatcherAssert.assertThat
import org.testng.annotations.Test

class AddLikesTest: BaseTest() {

    @Test
    fun addLikeTest() {
        val addLikeResult = addLike(AddLikeParams())
        assertThat("", addLikeResult.response.likesCount == 1L)
    }
}