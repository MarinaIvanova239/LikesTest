package com.vk.likes.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.entities.request.CheckLikeParams
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.entities.response.*
import com.vk.likes.services.AuthService.getOfflineScopeAccessToken
import com.vk.likes.services.AuthService.getWallScopeAccessToken
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

    private fun<T> mapWithStatusCheck(value: Response, valueType: Class<T>, objectDescription: String = "Value",
                                      mapper: ObjectMapper = mapper(), responsePath: String = "response"): T {
        assertThat("Request for $objectDescription should be successfully executed, but actual ${value.statusCode}",
                value.statusCode == 200)
        var resultBody = valueType.newInstance()
        try {
            val responseJsonNode = mapper.readTree(value.body.asString()).path(responsePath)
            assertThat("Expected $responsePath field to be present, but it was not", !responseJsonNode.isMissingNode)
            resultBody = mapper.treeToValue(responseJsonNode, valueType)
        } catch (e: Exception) {
            Assert.fail("Exception with message = ${e.message} occurred while processing json: $value")
        }
        assertThat("$objectDescription should not be null", nonNull(resultBody))
        return resultBody
    }

    private fun mapWithExpectingError(value: Response, objectDescription: String = "Value"): ErrorDescription {
        return mapWithStatusCheck(value, ErrorDescription::class.java, objectDescription, responsePath = "error")
    }

    private fun requestReturnTooManyRequestsError(value: Response): Boolean {
        val responseJsonNode = mapper().readTree(value.body.asString()).path("error")
        if (responseJsonNode.isMissingNode) return false
        if (mapper().treeToValue(responseJsonNode, ErrorDescription::class.java).code == 6) return true
        return false
    }

    private fun request(accessToken: String = getOfflineScopeAccessToken()): RequestSpecification {
        return RestAssured.given()
                .baseUri("https://api.vk.com/method/")
                .accept(ContentType.JSON)
                .param("access_token", accessToken)
                .param("v", "5.131")
    }

    fun addLike(addLikeParams: AddLikeParams): LikesCount {
        var addLikeRs = addLikeResponse(addLikeParams)
        for (i in 1..2) {
            if (requestReturnTooManyRequestsError(addLikeRs)) {
                Thread.sleep(1_000)
                addLikeRs = addLikeResponse(addLikeParams)
            } else break
        }
        return mapWithStatusCheck(addLikeRs, LikesCount::class.java, objectDescription = "add like")
    }

    fun addLikeWithExpectingError(addLikeParams: AddLikeParams, accessToken: String = getWallScopeAccessToken()): ErrorDescription {
        val addLikeRs = addLikeResponse(addLikeParams, accessToken)
        return mapWithExpectingError(addLikeRs, objectDescription = "add like")
    }

    private fun addLikeResponse(addLikeParams: AddLikeParams, accessToken: String = getWallScopeAccessToken()): Response {
        var addLikeRq = request(accessToken)
                .param("type", addLikeParams.objectType)
                .param("owner_id", addLikeParams.ownerId)
                .param("item_id", addLikeParams.objectId)
        if (nonNull(addLikeParams.accessKey)) addLikeRq = addLikeRq.param("access_key", addLikeParams.accessKey)
        return addLikeRq.post("likes.add")
    }

    fun deleteLike(deleteLikeParams: AddLikeParams): LikesCount {
        var deleteLikeRq = request(accessToken = getWallScopeAccessToken())
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

    fun getLikesWithExpectingError(getLikesParams: GetLikesParams): ErrorDescription {
        val getLikeRs = getLikesResponse(getLikesParams)
        return mapWithExpectingError(getLikeRs, objectDescription = "add like")
    }

    private fun getLikesResponse(getLikesParams: GetLikesParams): Response {
        var getLikesRq =  request()
                .param("type", getLikesParams.objectType)
                .param("owner_id", getLikesParams.ownerId)
                .param("item_id", getLikesParams.objectId)
                .param("filter", getLikesParams.filter)
                .param("skip_own", getLikesParams.skipOwn)

        if (nonNull(getLikesParams.pageUrl)) getLikesRq = getLikesRq.param("page_url", getLikesParams.pageUrl)
        if (nonNull(getLikesParams.friendsOnly)) getLikesRq = getLikesRq.param("friends_only", getLikesParams.friendsOnly)
        if (nonNull(getLikesParams.extended)) getLikesRq = getLikesRq.param("extended", getLikesParams.extended)
        if (nonNull(getLikesParams.offset)) getLikesRq = getLikesRq.param("offset", getLikesParams.offset)
        if (nonNull(getLikesParams.count)) getLikesRq = getLikesRq.param("count", getLikesParams.count)

        return getLikesRq.get("likes.getList")
    }

}