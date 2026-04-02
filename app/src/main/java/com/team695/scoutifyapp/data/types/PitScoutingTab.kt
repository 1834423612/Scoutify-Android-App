package com.team695.scoutifyapp.data.types

import java.time.Instant
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
data class PitImageAsset(
    val id: String = "",
    val url: String = "",
    val name: String,
    val size: Long,
    val localPath: String? = null,
    val mimeType: String? = null,
    val uploaded: Boolean = false
)

@Serializable
data class PitImageBundle(
    val fullRobotImages: List<PitImageAsset> = emptyList(),
    val driveTrainImages: List<PitImageAsset> = emptyList(),
    val intakeImages: List<PitImageAsset> = emptyList()
)

enum class PitScoutingStatus {
    DRAFT,
    DIRTY,
    PENDING_SUBMISSION,
    SUBMITTING,
    SUBMITTED,
    FAILED
}

data class PitScoutingTab(
    val tabId: String = UUID.randomUUID().toString(),
    val tabName: String = "New Tab",
    val teamNumber: String = "",
    val eventKey: String = "",
    val formId: String = UUID.randomUUID().toString(),
    val formVersion: String = "",
    val fields: List<PitFormField> = emptyList(),
    val images: PitImageBundle = PitImageBundle(),
    val isDraft: Boolean = true,
    val isSubmitted: Boolean = false,
    val submissionTime: String? = null,
    val createdAt: String = nowIsoString(),
    val updatedAt: String = nowIsoString(),
    val syncStatus: PitScoutingStatus = PitScoutingStatus.DRAFT,
    val lastError: String? = null
) {
    val completionRatio: Float
        get() {
            val requiredFields = fields.filter { it.required }
            if (requiredFields.isEmpty()) {
                return 0f
            }

            val completed = requiredFields.count { field ->
                when (val current = field.value) {
                    PitFieldValue.Empty -> false
                    is PitFieldValue.MultiValue -> current.values.isNotEmpty()
                    is PitFieldValue.TextValue -> current.value.isNotBlank()
                }
            }
            return completed.toFloat() / requiredFields.size.toFloat()
        }
}

data class PitAssignment(
    val id: String,
    val taskType: String,
    val assignedTeamNumbers: List<String>
)

data class PitTeamStatus(
    val teamNumber: String,
    val isPitCompleted: Boolean
)

data class SubmissionUserData(
    val username: String,
    val displayName: String,
    val userId: String,
    val avatar: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val groups: List<String> = emptyList(),
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList()
)

data class TeamSuggestion(
    val teamNumber: String,
    val teamName: String
)

private fun nowIsoString(): String = Instant.now().toString()
