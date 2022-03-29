package com.vk.likes.services

import com.vk.likes.entities.request.AddLikeParams
import com.vk.likes.entities.request.CheckLikeParams
import com.vk.likes.entities.request.GetLikesParams
import com.vk.likes.entities.response.ErrorDescription
import com.vk.likes.entities.response.LikesCount
import com.vk.likes.entities.response.ObjectIsLiked
import com.vk.likes.entities.response.UserLikesList
import com.vk.likes.services.AuthService.getOfflineScopeAccessToken
import com.vk.likes.services.AuthService.getWallScopeAccessToken
import io.restassured.response.Response
import java.util.*

object LikeService: RestService() {

    // add like
    fun addLike(addLikeParams: AddLikeParams, accessToken: String = getWallScopeAccessToken()): LikesCount {
        val addLikeRs = executeRequestIgnoringTooManyRequests(addLikeParams, accessToken, ::addLikeResponse)
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
        if (Objects.nonNull(addLikeParams.accessKey)) addLikeRq = addLikeRq.param("access_key", addLikeParams.accessKey)
        return addLikeRq.post("likes.add")
    }

    // delete like
    fun deleteLike(deleteLikeParams: AddLikeParams): LikesCount {
        var deleteLikeRq = request(accessToken = getWallScopeAccessToken())
                .param("type", deleteLikeParams.objectType)
                .param("owner_id", deleteLikeParams.ownerId)
                .param("item_id", deleteLikeParams.objectId)
        if (Objects.nonNull(deleteLikeParams.accessKey)) deleteLikeRq = deleteLikeRq.param("access_key", deleteLikeParams.accessKey)
        val addLikeRs = deleteLikeRq.post("likes.delete")

        return mapWithStatusCheck(addLikeRs, LikesCount::class.java, objectDescription = "delete like")
    }

    // check like
    fun checkIfObjectIsLiked(checkLikeParams: CheckLikeParams): ObjectIsLiked {
        var checkLikeRs = request()
                .param("type", checkLikeParams.objectType)
                .param("owner_id", checkLikeParams.ownerId)
                .param("item_id", checkLikeParams.objectId)
                .param("userId", checkLikeParams.objectType)
                .get("likes.isLiked")

        return mapWithStatusCheck(checkLikeRs, ObjectIsLiked::class.java, objectDescription = "check if like exist")
    }

    // get likes
    fun getLikes(getLikesParams: GetLikesParams, accessToken: String = getOfflineScopeAccessToken()): UserLikesList {
        val getLikesRs = executeRequestIgnoringTooManyRequests(getLikesParams, accessToken, ::getLikesResponse)
        return mapWithStatusCheck(getLikesRs, UserLikesList::class.java, objectDescription = "get user likes")
    }

    fun getLikesWithExpectingError(getLikesParams: GetLikesParams): ErrorDescription {
        val getLikeRs = getLikesResponse(getLikesParams)
        return mapWithExpectingError(getLikeRs, objectDescription = "add like")
    }

    private fun getLikesResponse(getLikesParams: GetLikesParams, accessToken: String = getOfflineScopeAccessToken()): Response {
        var getLikesRq =  request(accessToken)
                .param("type", getLikesParams.objectType)
                .param("owner_id", getLikesParams.ownerId)
                .param("item_id", getLikesParams.objectId)
                .param("filter", getLikesParams.filter)
                .param("skip_own", getLikesParams.skipOwn)

        if (Objects.nonNull(getLikesParams.pageUrl)) getLikesRq = getLikesRq.param("page_url", getLikesParams.pageUrl)
        if (Objects.nonNull(getLikesParams.friendsOnly)) getLikesRq = getLikesRq.param("friends_only", getLikesParams.friendsOnly)
        if (Objects.nonNull(getLikesParams.extended)) getLikesRq = getLikesRq.param("extended", getLikesParams.extended)
        if (Objects.nonNull(getLikesParams.offset)) getLikesRq = getLikesRq.param("offset", getLikesParams.offset)
        if (Objects.nonNull(getLikesParams.count)) getLikesRq = getLikesRq.param("count", getLikesParams.count)

        return getLikesRq.get("likes.getList")
    }
}