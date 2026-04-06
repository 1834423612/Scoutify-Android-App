package com.team695.scoutifyapp.data.repository

import android.util.Log
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.model.CommentBody
import com.team695.scoutifyapp.data.api.model.CommentServerBody
import com.team695.scoutifyapp.data.api.model.convertToServerBody
import com.team695.scoutifyapp.data.api.service.CommentService
import com.team695.scoutifyapp.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommentRepository (
    private val db: AppDatabase,
    private val service: CommentService
): Repository {

    override suspend fun push(): Result<List<CommentServerBody>> {
        return withContext(Dispatchers.IO) {
            try {
                val submittedComments = db.commentsQueries.selectPendingSubmittedComments()
                    .executeAsList()
                val commentServerFormat = submittedComments.map { it.convertToServerBody() }

                if (submittedComments.isNotEmpty()) {
                    service.uploadComments(
                        acToken = ScoutifyClient.tokenManager.getToken()!!,
                        comments = commentServerFormat
                    )

                    LocalDatabaseWriteCoordinator.withWriteLock {
                        db.transaction {
                            submittedComments.forEach { comment ->
                                db.commentsQueries.markCommentUploaded(comment.id)
                            }
                        }
                    }

                    Log.d("Comments", "Comments uploaded successfully")
                } else {
                    Log.d("Comments", "No comments to upload")
                }


                return@withContext Result.success(commentServerFormat)
            } catch (e: Exception) {
                Log.e("Comments", "Error uploading comments: $e")

                return@withContext Result.failure(e)
            }
        }
    }

    suspend fun saveComments(
        comments: List<CommentBody>
    ) {
        withContext(Dispatchers.IO) {
            LocalDatabaseWriteCoordinator.withWriteLock {
                db.transaction {
                    comments.forEach { c ->
                        val existingComment = db.commentsQueries.selectCommentByMatchAndTeam(
                            match_number = c.match_number,
                            team_number = c.team_number
                        ).executeAsOneOrNull()
                        val uploaded = existingComment?.uploaded == true &&
                            existingComment.comment == c.comment &&
                            existingComment.submitted == c.submitted &&
                            existingComment.alliance == c.alliance &&
                            existingComment.alliance_position == c.alliance_position

                        db.commentsQueries.insertComment(
                            c.match_number,
                            c.team_number,
                            c.alliance,
                            c.alliance_position,
                            c.timestamp,
                            c.comment,
                            c.submitted,
                            uploaded
                        )
                    }
                }
            }
        }
    }

    // Function to print all rows in the commentsEntity table
    suspend fun printAllComments() {
        // Fetch all comments from the database
        val allComments = db.commentsQueries.selectAllComments().executeAsList()

        // Print each comment
        allComments.forEach { comment ->
            Log.d("Comments", "Comment ID: ${comment.id}")
            Log.d("Comments", "Match Number: ${comment.match_number}")
            Log.d("Comments", "Team Number: ${comment.team_number}")
            Log.d("Comments", "Alliance: ${comment.alliance}")
            Log.d("Comments", "Alliance Position: ${comment.alliance_position}")
            Log.d("Comments", "Timestamp: ${comment.timestamp}")
            Log.d("Comments", "Comment: ${comment.comment}")
            Log.d("Comments", "Submitted: ${comment.submitted}")
            Log.d("Comments", "Uploaded: ${comment.uploaded}")
            Log.d("Comments", "----------")
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
        withContext(Dispatchers.IO) {
            LocalDatabaseWriteCoordinator.withWriteLock {
                db.commentsQueries.updateSubmissionStatus(submitted, matchNumber)
            }
        }
    }


}
