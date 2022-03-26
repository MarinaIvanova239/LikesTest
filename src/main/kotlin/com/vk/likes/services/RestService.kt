package com.vk.likes.services

import com.vk.likes.entities.request.AddLike
import com.vk.likes.entities.request.GetLikes
import com.vk.likes.services.AuthService.getAccessToken
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.hamcrest.MatcherAssert.assertThat

object RestService {

    private fun request(): RequestSpecification {
        return RestAssured.given()
                .baseUri("https://api.vk.com/method/")
                .accept(ContentType.JSON)
                .auth().oauth2(getAccessToken())
    }

    fun addLike(like: AddLike) {
        val addLikeRs = request().post("likes.add")
        assertThat("Like should be successfully added", addLikeRs.statusCode == 200)
    }

    fun getLike(like: GetLikes) {
        val getLikesRs = request().body(like).get("likes.getList")
        assertThat("List of users with likes should be successfully gotten", getLikesRs.statusCode == 200)
    }
}