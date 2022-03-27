package com.vk.likes.rest

import com.vk.likes.base.BaseTest
import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.entities.request.CheckLikeParams
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.services.RestService.addLike
import com.vk.likes.services.RestService.addLikeResponse
import com.vk.likes.services.RestService.checkIfObjectIsLiked
import com.vk.likes.services.RestService.deleteLike
import com.vk.likes.services.RestService.getLikes
import org.hamcrest.MatcherAssert.assertThat
import org.testng.annotations.AfterMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.Objects.nonNull

class AddLikesTest: BaseTest() {
    var likeParams: AddLikeParams? = null

    @AfterMethod(alwaysRun = true)
    fun removeLikeAfterTest() {
        if (nonNull(likeParams)) deleteLike(likeParams!!)
        likeParams = null
    }

    @DataProvider(name = "addLikesParamsAndExpectedLikesCount")
    fun addLikesParamsAndExpectedLikesCount(): MutableIterator<Array<AddLikeParams>> {
        // empty users list
        val addLikeParams1 = AddLikeParams(
        )

        return arrayListOf(
                arrayOf(addLikeParams1)
        ).iterator()
    }

    @Test(dataProvider = "addLikesParamsAndExpectedLikesCount")
    fun addLikeTest(addLikeParams: AddLikeParams) {
        // given
        likeParams = addLikeParams
        val likesCountBeforeTest = getLikes(GetLikesParams()).userCount
        // when
        val addLikeResult = addLike(addLikeParams)
        // then
        assertThat("Likes count should be increased by 1", addLikeResult.likesCount == likesCountBeforeTest + 1L)
        assertThat("Object should be in user's likes list", checkIfObjectIsLiked(CheckLikeParams()).isLiked == 1)
    }

    @Test
    fun addLikeToPrivateObjectWithoutAccessToken() {
        // given
        val addLikeParams = AddLikeParams(objectId = 0)
        // when
        val addLikeResult = addLikeResponse(addLikeParams)
        // then
        assertThat("Expect that reaction cannot be applied to object, but actual ${addLikeResult.statusCode}",
                addLikeResult.statusCode == 30)
    }

    @Test
    fun addLikeToNonExistentObject() {
        // given
        val addLikeParams = AddLikeParams(objectId = 0)
        // when
        val addLikeResult = addLikeResponse(addLikeParams)
        // then
        assertThat("Expect that reaction cannot be applied to object, but actual ${addLikeResult.statusCode}",
                addLikeResult.statusCode == 404)
    }

    @Test
    fun addLikeToObjectWithInvalidType() {
        // given
        val addLikeParams = AddLikeParams(objectId = 0)
        val likesCountBeforeTest = getLikes(GetLikesParams()).userCount
        // when
        val addLikeResult = addLikeResponse(addLikeParams)
        // then
        assertThat("Expect that reaction cannot be applied to object, but actual ${addLikeResult.statusCode}",
                addLikeResult.statusCode == 232)
        assertThat("Likes count should not increase", getLikes(GetLikesParams()).userCount == likesCountBeforeTest)
    }
}