package com.team695.scoutifyapp.data.api.service

import com.team695.scoutifyapp.data.api.model.CommentBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CommentService {
    @POST("upload-comments") // TEMPORARY BECAUSE ENDPOINT NOT CREATED YET
    suspend fun uploadComments(
        @Body comments: List<CommentBody> // sends a list of the comments, idk how this will work since the endpoint isnt implmeneted yet
    ): Response<Unit>
}