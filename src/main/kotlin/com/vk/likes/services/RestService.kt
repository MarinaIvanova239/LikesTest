package com.vk.likes.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.entities.request.CheckLikeParams
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.entities.response.*
import com.vk.likes.services.AuthService.getAccessToken
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import org.hamcrest.MatcherAssert.assertThat
import org.testng.Assert
import java.util.Objects.nonNull

object RestService {

    private fun mapper(): ObjectMapper {
        return ObjectMapper()
    }

    private fun<T> mapWithStatusCheck(value: Response, valueType: Class<T>, objectDescription: String = "Value"): T {
        assertThat("Request for $objectDescription should be successfully executed, but actual ${value.statusCode}",
                value.statusCode == 200)
        var resultBody = valueType.newInstance()
        try {
            val responseJsonNode = mapper().readTree(value.body.asString()).path("response")
            assertThat("Response field should be present", !responseJsonNode.isMissingNode)
            resultBody = mapper().readValue(responseJsonNode.asText(), valueType)
        } catch (e: Exception) {
            Assert.fail("Exception with message = ${e.message} occurred while processing json: $value")
        }
        assertThat("$objectDescription should not be null", nonNull(resultBody))
        return resultBody
    }

    private fun request(): RequestSpecification {
        return RestAssured.given()
                .baseUri("https://api.vk.com/method/")
                .accept(ContentType.JSON)
                .param("access_token", getAccessToken())
                .param("v", "5.131")
    }

    fun addLike(addLikeParams: AddLikeParams): LikesCount {
        val addLikeRs = addLikeResponse(addLikeParams)
        return mapWithStatusCheck(addLikeRs, LikesCount::class.java, objectDescription = "add like")
    }

    fun addLikeResponse(addLikeParams: AddLikeParams): Response {
        var addLikeRq = request()
                .param("type", addLikeParams.objectType)
                .param("owner_id", addLikeParams.ownerId)
                .param("item_id", addLikeParams.objectId)
        if (nonNull(addLikeParams.accessKey)) addLikeRq = addLikeRq.param("access_key", addLikeParams.accessKey)
        return addLikeRq.post("likes.add")
    }

    fun deleteLike(deleteLikeParams: AddLikeParams): LikesCount {
        var deleteLikeRq = request()
                .param("type", deleteLikeParams.objectType)
                .param("owner_id", deleteLikeParams.ownerId)
                .param("item_id", deleteLikeParams.objectId)
        if (nonNull(deleteLikeParams.accessKey)) deleteLikeRq = deleteLikeRq.param("access_key", deleteLikeParams.accessKey)
        val addLikeRs = deleteLikeRq.post("likes.delete")

        return mapWithStatusCheck(addLikeRs, LikesCount::class.java, objectDescription = "delete like")
    }

    fun checkIfObjectIsLiked(checkLikeParams: CheckLikeParams): ObjectIsLiked {
        var checkLikeRs = request()
                .param("type", checkLikeParams.objectType)
                .param("owner_id", checkLikeParams.ownerId)
                .param("item_id", checkLikeParams.objectId)
                .param("userId", checkLikeParams.objectType)
                .get("likes.isLiked")

        return mapWithStatusCheck(checkLikeRs, ObjectIsLiked::class.java, objectDescription = "check if like exist")
    }

    fun getLikes(getLikesParams: GetLikesParams): UserLikesList {
        val getLikesRs = getLikesResponse(getLikesParams)
        return mapWithStatusCheck(getLikesRs, UserLikesList::class.java, objectDescription = "get user likes")
    }

    fun getLikesResponse(getLikesParams: GetLikesParams): Response {
        return request()
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
    }
}