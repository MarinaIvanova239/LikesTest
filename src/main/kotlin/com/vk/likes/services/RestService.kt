package com.vk.likes.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.entities.request.CheckLikeParams
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.entities.response.LikesCountRs
import com.vk.likes.entities.response.ObjectIsLikedRs
import com.vk.likes.entities.response.UserLikesListRs
import com.vk.likes.services.AuthService.getAccessToken
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.hamcrest.MatcherAssert.assertThat
import java.util.Objects.nonNull

object RestService {

    private fun mapper(): ObjectMapper {
        return ObjectMapper()
    }

    private fun request(): RequestSpecification {
        return RestAssured.given()
                .baseUri("https://api.vk.com/method/")
                .accept(ContentType.JSON)
                .param("access_token", getAccessToken())
                .param("v", "5.131")
    }

    fun addLike(addLikeParams: AddLikeParams): LikesCountRs {
        var addLikeRq = request()
                .param("type", addLikeParams.objectType)
                .param("owner_id", addLikeParams.ownerId)
                .param("item_id", addLikeParams.objectId)
        if (nonNull(addLikeParams.accessKey)) addLikeRq = addLikeRq.param("access_key", addLikeParams.accessKey)
        val addLikeRs = addLikeRq.post("likes.add")
        assertThat("Like should be successfully added", addLikeRs.statusCode == 200)
        return mapper().readValue(addLikeRs.body.toString(), LikesCountRs::class.java)
    }

    fun checkIfObjectIsLiked(checkLikeParams: CheckLikeParams): ObjectIsLikedRs {
        var checkLikeRs = request()
                .param("type", checkLikeParams.objectType)
                .param("owner_id", checkLikeParams.ownerId)
                .param("item_id", checkLikeParams.objectId)
                .param("userId", checkLikeParams.objectType)
                .get("likes.isLiked")
        assertThat("Like should be successfully added", checkLikeRs.statusCode == 200)
        return mapper().readValue(checkLikeRs.body.toString(), ObjectIsLikedRs::class.java)
    }

    fun getLike(getLikesParams: GetLikesParams): UserLikesListRs {
        val getLikesRs = request()
                .param("type", getLikesParams.objectType)
                .param("owner_id", getLikesParams.ownerId)
                .param("item_id", getLikesParams.objectId)
                .param("page_url", getLikesParams.pageUrl)
                .param("filter", getLikesParams.filter)
                .param("friends_only", getLikesParams.friendsOnly)
                .param("extended", getLikesParams.extended)
                .param("offset", getLikesParams.offset)
                .param("count", getLikesParams.count)
                .param("skip_own", getLikesParams.skipOwn)
                .get("likes.getList")
        assertThat("List of users with likes should be successfully gotten", getLikesRs.statusCode == 200)
        return mapper().readValue(getLikesRs.body.toString(), UserLikesListRs::class.java)
    }
}