package com.vk.likes.rest

import com.vk.likes.base.BaseTest
import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.entities.response.UserLikesList
import com.vk.likes.services.RestService.getLikes
import com.vk.likes.services.RestService.getLikesResponse
import org.hamcrest.MatcherAssert.assertThat
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals
import org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER

class GetLikesTest: BaseTest() {

    @DataProvider(name = "likesParamsAndUsersList")
    fun likesParamsAndUsersList(): MutableIterator<Array<Any>> {
        // empty users list
        val getLikesParams1 = AddLikeParams(

        )

        val usersList1 = UserLikesList(
                userCount = 0,
                userIds = emptyList()
        )

        // 1 user list
        val getLikesParams2 = GetLikesParams(

        )

        val usersList2 = UserLikesList(
                userCount = 0,
                userIds = emptyList()
        )

        // > 100 users list
        val getLikesParams3 = GetLikesParams(

        )

        val usersList3 = UserLikesList(
                userCount = 0,
                userIds = emptyList()
        )

        return arrayListOf(
                arrayOf(getLikesParams1, usersList1),
                arrayOf(getLikesParams2, usersList2),
                arrayOf(getLikesParams3, usersList3)
        ).iterator()
    }

    @Test(dataProvider = "likesParamsAndUsersList")
    fun getLikesWithDifferentUsersNumberTest(getLikesParams: GetLikesParams, expectedUserLikesList: UserLikesList) {
        val actualUserLikesList = getLikes(getLikesParams)
        assertReflectionEquals(expectedUserLikesList, actualUserLikesList, LENIENT_ORDER)
    }

    @Test
    fun getLikesWhenObjectDoesNotExist() {
        val getLikesRs = getLikesResponse(GetLikesParams(objectId = 0))
        assertThat("Expect object cannot be found error, but actual ${getLikesRs.statusCode}",
                getLikesRs.statusCode == 404)
    }

    @Test
    fun getLikesWhenReactionCannotBeAppliedToObject() {
        val getLikesRs = getLikesResponse(GetLikesParams(objectId = 0))
        assertThat("Expect that reaction cannot be applied to object, but actual ${getLikesRs.statusCode}",
                getLikesRs.statusCode == 232)
    }

    @Test
    fun getRepostInformationWhenRequestIsNotFromOwner() {
        val getLikesRs = getLikesResponse(GetLikesParams(objectId = 0))
        assertThat("Expect that repost information cannot be get by not owner, but actual ${getLikesRs.statusCode}",
                getLikesRs.statusCode == 232)
    }
}