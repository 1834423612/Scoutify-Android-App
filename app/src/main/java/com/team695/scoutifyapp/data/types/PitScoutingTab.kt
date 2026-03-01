package com.team695.scoutifyapp.data.types

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents a single pit scouting tab/session for a team
 */
data class PitScoutingTab(
    val tabId: String = UUID.randomUUID().toString(),
    val teamNumber: String = "",
    val eventKey: String = "",
    val formId: String = UUID.randomUUID().toString(),
    val formVersion: String = "2025.4.15_PROD_ED6",
    val fieldValues: Map<Int, Any?> = emptyMap(),
    val uploadData: Map<String, List<UploadFile>> = emptyMap(), // fullRobotImages, driveTrainImages, etc.
    val isDraft: Boolean = true,
    val isSubmitted: Boolean = false,
    val submissionTime: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class UploadFile(
    val url: String,
    val name: String,
    val size: Long
)

/**
 * Survey response structure matching the API format
 */
data class SurveyResponse(
    val id: String? = null, // Server generates this
    val eventId: String,
    val formId: String,
    val data: Map<String, Any?>, // Form field values
    val upload: Map<String, List<UploadFile>>? = null, // Images and other uploads
    val userData: UserDataPayload,
    val userAgent: String,
    val ip: String? = null,
    val language: String = "en-US",
    val timestamp: String? = null // Server generates this
)

data class UserDataPayload(
    val email: String,
    val avatar: String = "",
    val userId: String,
    val username: String,
    val displayName: String
)

/**
 * Status for a pit scouting submission
 */
enum class PitScoutingStatus {
    DRAFT,
    PENDING_SUBMISSION,
    SUBMITTED,
    FAILED
}
