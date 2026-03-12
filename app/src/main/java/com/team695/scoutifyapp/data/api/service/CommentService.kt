package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.BuildConfig
import com.team695.scoutifyapp.data.api.model.CommentBody
import com.team695.scoutifyapp.data.api.model.CommentServerBody
import com.team695.scoutifyapp.db.CommentsEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CommentService {
    @POST("scoutify/game-comments")
    suspend fun uploadComments(
        @Header("Authorization") acToken: String,
        @Header("X-API-Key") acKey: String
            = BuildConfig.API_AC_KEY,
        @Header("X-API-Secret") secret: String
            = BuildConfig.API_AC_SECRET,
        @Body comments: List<CommentServerBody>
    ): ApiResponse<Any>
}