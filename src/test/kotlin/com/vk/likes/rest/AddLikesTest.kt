package com.vk.likes.rest

import com.vk.likes.base.BaseTest
import com.vk.likes.config.Config.clientId
import com.vk.likes.entities.ObjectType
import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.entities.request.CheckLikeParams
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.services.AuthService.getOfflineScopeAccessToken
import com.vk.likes.services.RestService.addLike
import com.vk.likes.services.RestService.addLikeWithExpectingError
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

    @DataProvider(name = "addLikesParamsAndCurrentUserId")
    fun addLikesParamsAndUserId(): MutableIterator<Array<AddLikeParams>> {
        // like post of profile
        val addLikeToOpenProfileParams = AddLikeParams(
                ownerId = 9599485,
                objectId = 4713
        )

        // like post of group
        val addLikeToOpenGroupParams = AddLikeParams(
                ownerId = -22822305,
                objectId = 1286624
        )

        // like photo
        val addLikeToPublicPhotoParams = AddLikeParams(
                objectType = ObjectType.photo,
                ownerId = -22822305,
                objectId = 457330266
        )

        return arrayListOf(
                arrayOf(addLikeToOpenProfileParams),
                arrayOf(addLikeToOpenGroupParams),
                arrayOf(addLikeToPublicPhotoParams)
        ).iterator()
    }

    @Test(dataProvider = "addLikesParamsAndCurrentUserId")
    fun likesCanBeAddedToDifferentObjectsTest(addLikeParams: AddLikeParams) {
        // given
        likeParams = addLikeParams
        val likesCountBeforeTest = getLikes(GetLikesParams(
                ownerId = addLikeParams.ownerId,
                objectId = addLikeParams.objectId,
                objectType = addLikeParams.objectType)
        ).userCount
        // when
        val addLikeResult = addLike(addLikeParams)
        // then
        assertThat("Likes count should be increased by 1", addLikeResult.likesCount == likesCountBeforeTest + 1L)
        assertThat("Object should be in user's likes list", checkIfObjectIsLiked(
                CheckLikeParams(ownerId = addLikeParams.ownerId,
                        objectType = addLikeParams.objectType,
                        objectId = addLikeParams.objectId,
                        userId = clientId))
                .isLiked == 1)
    }

    @Test
    fun likesCannotBeAddedToObjectOnPrivatePage() {
        // given
        val userIdWithPrivatePage = 85645519
        val likePrivateObjectParams = AddLikeParams(ownerId = userIdWithPrivatePage, objectId = 1)
        // when
        val errorDescription = addLikeWithExpectingError(likePrivateObjectParams)
        // then
        assertThat("Expect status code = 30, but actual ${errorDescription.code}", errorDescription.code == 30)
        assertThat("Expect description that profile is private", errorDescription.message.contains("This profile is private"))
    }

    @Test
    fun errorShouldBeReturnedWhenApplyingLikeToNonExistentObject() {
        // given
        val nonExistentObjectParams = AddLikeParams(ownerId = -22822305, objectId = Int.MAX_VALUE)
        // when
        val errorDescription = addLikeWithExpectingError(nonExistentObjectParams)
        // then
        assertThat("Expect status code = 100, but actual = ${errorDescription.code}", errorDescription.code == 100)
        assertThat("Expect description that object cannot be found", errorDescription.message.contains("object not found"))
    }

    @Test
    fun likeCannotBeAddedToProjectIfAccessTokenDoesNotHaveWallScope() {
        // given
        val tokenWithIncorrectScope = getOfflineScopeAccessToken()
        val publicPostParams = AddLikeParams(ownerId = -22822305, objectId = 1286624)
        val likesCountBeforeTest = getLikes(GetLikesParams(objectId = publicPostParams.objectId, ownerId = publicPostParams.ownerId)).userCount
        // when
        val errorDescription = addLikeWithExpectingError(publicPostParams, accessToken = tokenWithIncorrectScope)
        // then
        assertThat("Expect status code = 15, but actual ${errorDescription.code}", errorDescription.code == 15)
        assertThat("Expect error description that access is denied", errorDescription.message.contains("Access denied"))
        assertThat("Likes count should not change", getLikes(GetLikesParams(objectId = publicPostParams.objectId,
                ownerId = publicPostParams.ownerId)).userCount == likesCountBeforeTest)
    }
}