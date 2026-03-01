package com.team695.scoutifyapp.data.repository

import android.util.Log
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.CommentBody
import com.team695.scoutifyapp.data.api.model.convertToServerBody
import com.team695.scoutifyapp.data.api.service.CommentService
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommentRepository (
    private val db: AppDatabase,
    private val service: CommentService
) {

    suspend fun uploadComments() {
        withContext(Dispatchers.IO) {
            try {
                val allComments = db.commentsQueries.selectAllComments().executeAsList()

                service.uploadComments(
                    acToken = ScoutifyClient.tokenManager.getToken()!!,
                    comments = allComments.map { it.convertToServerBody() }
                )
            } catch (e: Exception) {
                Log.e("Comments", "Error uploading comments: $e")
            }
        }
    }

    suspend fun saveComments (
        comments: List<CommentBody>
    ) {
        withContext(Dispatchers.IO) {
            db.transaction {
                comments.forEach { c ->
                    db.commentsQueries.insertComment(
                        c.match_number,
                        c.team_number,
                        c.alliance,
                        c.alliance_position,
                        c.timestamp,
                        c.comment,
                        c.submitted
                    )
                }
            }

            uploadComments()
        }
    }

    // Function to print all rows in the commentsEntity table
    suspend fun printAllComments() {
        // Fetch all comments from the database
        val allComments = db.commentsQueries.selectAllComments().executeAsList()

        // Print each comment
        allComments.forEach { comment ->
            println("Comment ID: ${comment.id}")
            println("Match Number: ${comment.match_number}")
            println("Team Number: ${comment.team_number}")
            println("Alliance: ${comment.alliance}")
            println("Alliance Position: ${comment.alliance_position}")
            println("Timestamp: ${comment.timestamp}")
            println("Comment: ${comment.comment}")
            println("Submitted: ${comment.submitted}")
            println("----------")
        }
    }

    suspend fun getCommentsByMatchNumber(matchNumber: Int): List<CommentBody> {
        return db.commentsQueries.selectCommentsByMatchNumber(matchNumber)
            .executeAsList() // Get results as a list of CommentEntity
            .map { commentEntity ->
                // Map each CommentEntity to a CommentBody
                CommentBody(
                    match_number = commentEntity.match_number,
                    team_number = commentEntity.team_number,
                    comment = commentEntity.comment,
                    alliance = commentEntity.alliance,
                    alliance_position = commentEntity.alliance_position,
                    timestamp = commentEntity.timestamp,
                    submitted = commentEntity.submitted
                )
            }
    }


    // Update the 'submitted' value for a specific match
    suspend fun updateSubmissionStatus(matchNumber: Int, submitted: Int) {
        db.commentsQueries.updateSubmissionStatus(submitted, matchNumber)
    }


}
