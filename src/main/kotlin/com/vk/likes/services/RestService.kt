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

abstract class RestService {

    private fun mapper(): ObjectMapper {
        return ObjectMapper()
    }

    protected fun<T> executeRequestIgnoringTooManyRequests(params: T, token: String, request: (T, String) -> Response): Response {
        var likeRs = request(params, token)
        for (i in 1..2) {
            if (requestReturnTooManyRequestsError(likeRs)) {
                Thread.sleep(1_000)
                likeRs = request(params, token)
            } else break
        }
        return likeRs
    }

    protected fun<T> mapWithStatusCheck(value: Response, valueType: Class<T>, objectDescription: String = "Value",
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

    protected fun mapWithExpectingError(value: Response, objectDescription: String = "Value"): ErrorDescription {
        return mapWithStatusCheck(value, ErrorDescription::class.java, objectDescription, responsePath = "error")
    }

    protected fun requestReturnTooManyRequestsError(value: Response): Boolean {
        val responseJsonNode = mapper().readTree(value.body.asString()).path("error")
        if (responseJsonNode.isMissingNode) return false
        if (mapper().treeToValue(responseJsonNode, ErrorDescription::class.java).code == 6) return true
        return false
    }

    protected fun request(accessToken: String = getOfflineScopeAccessToken()): RequestSpecification {
        return RestAssured.given()
                .baseUri("https://api.vk.com/method/")
                .accept(ContentType.JSON)
                .param("access_token", accessToken)
                .param("v", "5.131")
    }
}