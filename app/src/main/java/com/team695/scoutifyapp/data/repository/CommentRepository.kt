package com.team695.scoutifyapp.data.repository

import com.team695.scoutifyapp.data.api.model.CommentBody
import com.team695.scoutifyapp.data.api.service.CommentService
import com.team695.scoutifyapp.db.AppDatabase

class CommentRepository (
    private val service: CommentService,
    private val db: AppDatabase
) {
    suspend fun saveComments (
        comments: List<CommentBody>
    ) {
        for (c in comments) {
            db.commentsQueries.insertComments(c.id, c.match_number, c.team_number, c.alliance, c.alliance_position, c.timestamp, c.comment, c.submitted)
        }
        val response = db.commentsQueries.insertComments()

    }
}
