package com.team695.scoutifyapp.data.repository

import com.team695.scoutifyapp.data.api.service.SurveyService
import com.team695.scoutifyapp.data.types.*
import com.team695.scoutifyapp.data.api.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.util.UUID

/**
 * Repository for managing pit scouting tabs, local data persistence, and API synchronization
 * 
 * NOTE: This is a template implementation. Database integration will be added
 * when AppDatabase is properly configured with SQLDelight.
 */
class PitScoutingRepository(
    private val surveyService: SurveyService,
    private val networkMonitor: NetworkMonitor
) {
    
    // In-memory storage for tabs (will be replaced with database operations)
    private val tabsStore = mutableMapOf<String, PitScoutingTab>()

    // ==================== Tab Management ====================

    /**
     * Get all tabs for a specific event
     */
    fun getTabsForEvent(eventKey: String): Flow<List<PitScoutingTab>> {
        val filteredTabs = tabsStore.values
            .filter { it.eventKey == eventKey }
            .sortedByDescending { it.updatedAt }
        return flowOf(filteredTabs)
    }

    /**
     * Get a specific tab by ID
     */
    fun getTabById(tabId: String): Flow<PitScoutingTab?> {
        return flowOf(tabsStore[tabId])
    }

    /**
     * Create a new tab
     */
    suspend fun createNewTab(
        teamNumber: String,
        eventKey: String,
        formFields: List<PitFormField>
    ): PitScoutingTab {
        val tab = PitScoutingTab(
            tabId = UUID.randomUUID().toString(),
            teamNumber = teamNumber,
            eventKey = eventKey,
            formId = UUID.randomUUID().toString(),
            fieldValues = formFields.withIndex().associate { (index, _) -> index to null },
            isDraft = true,
            isSubmitted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        tabsStore[tab.tabId] = tab
        return tab
    }

    /**
     * Update tab field values
     */
    suspend fun updateTabFieldValue(tabId: String, fieldIndex: Int, value: Any?) {
        val tab = tabsStore[tabId] ?: return
        
        val updatedFieldValues = tab.fieldValues.toMutableMap()
        updatedFieldValues[fieldIndex] = value
        
        val updatedTab = tab.copy(
            fieldValues = updatedFieldValues,
            updatedAt = LocalDateTime.now()
        )
        
        tabsStore[tabId] = updatedTab
    }

    /**
     * Update tab upload data (images, etc.)
     */
    suspend fun updateTabUploadData(
        tabId: String,
        uploadType: String,
        files: List<UploadFile>
    ) {
        val tab = tabsStore[tabId] ?: return
        
        val updatedUploadData = tab.uploadData.toMutableMap()
        updatedUploadData[uploadType] = files
        
        val updatedTab = tab.copy(
            uploadData = updatedUploadData,
            updatedAt = LocalDateTime.now()
        )
        
        tabsStore[tabId] = updatedTab
    }

    /**
     * Save tab as draft
     */
    suspend fun saveDraft(tabId: String) {
        val tab = tabsStore[tabId] ?: return
        
        val updatedTab = tab.copy(
            isDraft = true,
            isSubmitted = false,
            updatedAt = LocalDateTime.now()
        )
        
        tabsStore[tabId] = updatedTab
    }

    /**
     * Delete a tab
     */
    suspend fun deleteTab(tabId: String) {
        tabsStore.remove(tabId)
    }

    // ==================== Submission & Sync ====================

    /**
     * Submit a tab to the API
     * If offline, mark as pending and sync when online
     */
    suspend fun submitTab(tabId: String, accessToken: String = ""): Result<Unit> {
        return try {
            val tab = tabsStore[tabId]
                ?: return Result.failure(Exception("Tab not found"))

            val userDataPayload = UserDataPayload(
                email = "scout@team695.com",
                userId = "debug-user-id",
                username = "scout_user",
                displayName = "Scout User"
            )

            val surveyResponse = SurveyResponse(
                eventId = tab.eventKey,
                formId = tab.formId,
                data = convertFormDataToApiFormat(tab.fieldValues),
                upload = if (tab.uploadData.isNotEmpty()) tab.uploadData else null,
                userData = userDataPayload,
                userAgent = getDeviceUserAgent(),
                language = "en-US"
            )

            // If online, submit immediately
            if (networkMonitor.isConnected.value) {
                try {
                    val response = surveyService.submitSurveyResponse(
                        surveyResponse = surveyResponse,
                        acToken = accessToken
                    )

                    if (response.success) {
                        val submittedTab = tab.copy(
                            isDraft = false,
                            isSubmitted = true,
                            submissionTime = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                        )
                        tabsStore[tabId] = submittedTab
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("API submission failed"))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            } else {
                // Mark as pending submission for later sync
                val pendingTab = tab.copy(
                    isDraft = false,
                    isSubmitted = false,
                    updatedAt = LocalDateTime.now()
                )
                tabsStore[tabId] = pendingTab
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync pending submissions when network becomes available
     */
    suspend fun syncPendingSubmissions(accessToken: String = ""): Result<Int> {
        return try {
            val pendingTabs = tabsStore.values.filter { !it.isDraft && !it.isSubmitted }
            var successCount = 0

            for (tab in pendingTabs) {
                val userDataPayload = UserDataPayload(
                    email = "scout@team695.com",
                    userId = "debug-user-id",
                    username = "scout_user",
                    displayName = "Scout User"
                )

                val surveyResponse = SurveyResponse(
                    eventId = tab.eventKey,
                    formId = tab.formId,
                    data = convertFormDataToApiFormat(tab.fieldValues),
                    upload = if (tab.uploadData.isNotEmpty()) tab.uploadData else null,
                    userData = userDataPayload,
                    userAgent = getDeviceUserAgent(),
                    language = "en-US"
                )

                try {
                    val response = surveyService.submitSurveyResponse(
                        surveyResponse = surveyResponse,
                        acToken = accessToken
                    )

                    if (response.success) {
                        val submittedTab = tab.copy(
                            isSubmitted = true,
                            submissionTime = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now()
                        )
                        tabsStore[tab.tabId] = submittedTab
                        successCount++
                    }
                } catch (e: Exception) {
                    // Continue with next pending submission
                    continue
                }
            }

            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== Utility Methods ====================

    /**
     * Convert form field values to API submission format
     */
    private fun convertFormDataToApiFormat(fieldValues: Map<Int, Any?>): Map<String, Any?> {
        val formFields = PitFormDataProvider.getDefaultFormFields()
        val result = mutableMapOf<String, Any?>()

        for ((index, value) in fieldValues) {
            if (index < formFields.size) {
                result[formFields[index].question] = value
            }
        }

        return result
    }

    /**
     * Get device user agent string
     */
    private fun getDeviceUserAgent(): String {
        val osVersion = android.os.Build.VERSION.RELEASE
        val deviceModel = android.os.Build.MODEL
        return "Android/$osVersion ($deviceModel)"
    }
}

