package com.vk.likes.rest

import com.vk.likes.base.BaseTest
import com.vk.likes.entities.FilterType.copies
import com.vk.likes.entities.ObjectType
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.entities.response.UserLikesList
import com.vk.likes.services.LikeService.getLikes
import com.vk.likes.services.LikeService.getLikesWithExpectingError
import org.hamcrest.MatcherAssert.assertThat
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals
import org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER

class GetLikesTest: BaseTest() {

    @DataProvider(name = "getLikesParamsAndExpectedUsersList")
    fun getLikesParamsAndExpectedUsersList(): MutableIterator<Array<Any>> {
        // empty users list
        val getZeroLikesParams = GetLikesParams(
                ownerId = 9599485,
                objectId = 4713
        )

        val expectedEmptyUserList = UserLikesList(
                userCount = 0,
                userIds = emptyList()
        )

        // 1 user list
        val getSingleLikeParams = GetLikesParams(
                ownerId = 9599485,
                objectId = 4712
        )

        val expectedSingleUserList = UserLikesList(
                userCount = 1,
                userIds = listOf(87445300)
        )

        // > 100 users list
        val getLikesWhenSizeMoreThanDefaultCount = GetLikesParams(
                objectType = ObjectType.photo,
                ownerId = 465830,
                objectId = 457239397
        )

        val expectedLargeUsersList = UserLikesList(
                userCount = 127,
                // here should be predefined list of users (or from database, for example)
                userIds = listOf()
        )

        return arrayListOf(
                arrayOf(getZeroLikesParams, expectedEmptyUserList),
                arrayOf(getSingleLikeParams, expectedSingleUserList),
                arrayOf(getLikesWhenSizeMoreThanDefaultCount, expectedLargeUsersList)
        ).iterator()
    }

    @Test(dataProvider = "getLikesParamsAndExpectedUsersList")
    fun getLikesWithDifferentUsersCountTest(getLikesParams: GetLikesParams, expectedUserLikesList: UserLikesList) {
        val actualUserLikesList = getLikes(getLikesParams)
        if (expectedUserLikesList.userCount <= 100) {
            assertReflectionEquals(expectedUserLikesList, actualUserLikesList, LENIENT_ORDER)
        } else {
            assertThat("Users count should be the same as expected", expectedUserLikesList.userCount == actualUserLikesList.userCount)
            assertThat("User ids list should be equal or less than 100", actualUserLikesList.userIds.size <= 100)
        }
    }

    @Test
    fun getInformationAboutLikesFromPrivatePageShouldReturnError() {
        // given
        val userIdWithPrivatePage = 85645519
        val getLikeParams = GetLikesParams(ownerId = userIdWithPrivatePage, objectId = 1)
        // when
        val errorDescription = getLikesWithExpectingError(getLikeParams)
        // then
        assertThat("Expect status code = 15, but actual ${errorDescription.code}", errorDescription.code == 15)
        assertThat("Expect error description that access is denied", errorDescription.message.contains("Access denied"))
    }

    @Test
    fun getLikesForNonExistentObject() {
        // given
        val getLikeParams = GetLikesParams(ownerId = -22822305, objectId = Int.MAX_VALUE)
        // when
        val errorDescription = getLikesWithExpectingError(getLikeParams)
        // then
        // actual behaviour is that object with count = 0 and empty users list is returned, but I expected error that object is not found
        assertThat("Expect status code = 100, but actual = ${errorDescription.code}", errorDescription.code == 100)
        assertThat("Expect description that object cannot be found", errorDescription.message.contains("object not found"))
    }

    @Test
    fun getCopiesOfObjectWhichWasCreatedByDifferentOwner() {
        // given
        val getLikeParams = GetLikesParams(ownerId = 9599485, objectId = 4700, filter = copies)
        // when
        val getCopiesResult = getLikes(getLikeParams)
        // then
        assertReflectionEquals(UserLikesList(userCount = 0, userIds = emptyList()), getCopiesResult, LENIENT_ORDER)
    }
}