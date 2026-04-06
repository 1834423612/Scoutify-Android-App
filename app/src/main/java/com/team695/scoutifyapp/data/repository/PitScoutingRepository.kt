package com.team695.scoutifyapp.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.webkit.MimeTypeMap
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.team695.scoutifyapp.data.api.NetworkMonitor
import com.team695.scoutifyapp.data.api.client.ScoutifyClient
import com.team695.scoutifyapp.data.api.service.CurrentEventResponse
import com.team695.scoutifyapp.data.api.service.PitAssignmentDto
import com.team695.scoutifyapp.data.api.service.SurveyService
import com.team695.scoutifyapp.data.api.service.TeamSuggestionDto
import com.team695.scoutifyapp.data.types.FieldType
import com.team695.scoutifyapp.data.types.PitAssignment
import com.team695.scoutifyapp.data.types.PitFormField
import com.team695.scoutifyapp.data.types.PitImageAsset
import com.team695.scoutifyapp.data.types.PitImageBundle
import com.team695.scoutifyapp.data.types.PitScoutingStatus
import com.team695.scoutifyapp.data.types.PitScoutingTab
import com.team695.scoutifyapp.data.types.SubmissionUserData
import com.team695.scoutifyapp.data.types.TeamSuggestion
import com.team695.scoutifyapp.data.types.valueAsList
import com.team695.scoutifyapp.data.types.valueAsText
import com.team695.scoutifyapp.db.AppDatabase
import com.team695.scoutifyapp.utility.OfflineSubmissionManager
import java.io.File
import java.time.Instant
import java.util.Locale
import java.util.UUID
import kotlin.math.absoluteValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class PitCurrentEvent(
    val eventId: String,
    val eventDisplayName: String
)

class PitScoutingRepository(
    private val db: AppDatabase,
    private val surveyService: SurveyService,
    private val networkMonitor: NetworkMonitor,
    private val userRepository: UserRepository,
    private val teamNameRepository: TeamNameRepository,
    private val context: Context,
    private val offlineSubmissionManager: OfflineSubmissionManager = OfflineSubmissionManager()
) : Repository {
    private val gson = Gson()
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun getTabsForEvent(eventKey: String): Flow<List<PitScoutingTab>> {
        return db.pitScoutingTabQueries.getAllTabsForEvent(eventKey)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row -> rowToTab(row) } }
            .flowOn(Dispatchers.IO)
    }

    suspend fun resolveCurrentEvent(defaultEventKey: String): PitCurrentEvent {
        return withContext(Dispatchers.IO) {
            val fallback = PitCurrentEvent(
                eventId = defaultEventKey,
                eventDisplayName = defaultEventKey
            )

            if (!networkMonitor.isConnected.value) {
                return@withContext fallback
            }

            runCatching { surveyService.getCurrentEvent() }
                .map { response -> response.toPitCurrentEvent() ?: fallback }
                .getOrDefault(fallback)
        }
    }

    suspend fun getTabById(tabId: String): PitScoutingTab? {
        return withContext(Dispatchers.IO) {
            db.pitScoutingTabQueries.getTabById(tabId)
                .executeAsOneOrNull()
                ?.let { row -> rowToTab(row) }
        }
    }

    suspend fun createNewTab(eventKey: String, teamNumber: String = ""): PitScoutingTab {
        return withContext(Dispatchers.IO) {
            val tabCount = db.pitScoutingTabQueries.getAllTabsForEvent(eventKey).executeAsList().size + 1
            val normalizedTeamNumber = normalizeTeamNumber(teamNumber)
            val tab = PitScoutingTab(
                tabId = UUID.randomUUID().toString(),
                tabName = buildTabName(normalizedTeamNumber, tabCount),
                teamNumber = normalizedTeamNumber,
                eventKey = eventKey,
                formId = UUID.randomUUID().toString(),
                formVersion = PitFormDataProvider.FORM_VERSION,
                fields = PitFormDataProvider.getDefaultFormFields(normalizedTeamNumber),
                images = PitImageBundle(),
                isDraft = true,
                isSubmitted = false,
                createdAt = nowIso(),
                updatedAt = nowIso(),
                syncStatus = PitScoutingStatus.DRAFT
            )
            persistTab(tab)
            tab
        }
    }

    suspend fun updateField(tabId: String, fieldIndex: Int, transform: (PitFormField) -> PitFormField): PitScoutingTab? {
        return withContext(Dispatchers.IO) {
            val existing = getTabById(tabId) ?: return@withContext null
            val updatedFields = existing.fields.map { field ->
                if (field.originalIndex == fieldIndex) transform(field) else field
            }
            val updatedTab = existing.asEdited(fields = updatedFields)
            persistTab(updatedTab)
            updatedTab
        }
    }

    suspend fun clearTab(tabId: String, regenerateFormId: Boolean = false): PitScoutingTab? {
        return withContext(Dispatchers.IO) {
            val existing = getTabById(tabId) ?: return@withContext null
            val cleared = existing.copy(
                teamNumber = "",
                tabName = buildFallbackTabName(existing.tabId),
                formId = if (regenerateFormId) UUID.randomUUID().toString() else existing.formId,
                fields = PitFormDataProvider.createEmptyFormFields(),
                images = PitImageBundle(),
                isDraft = true,
                isSubmitted = false,
                submissionTime = null,
                createdAt = if (regenerateFormId) nowIso() else existing.createdAt,
                updatedAt = nowIso(),
                syncStatus = PitScoutingStatus.DRAFT,
                lastError = null
            )
            deleteLocalFiles(existing.images)
            LocalDatabaseWriteCoordinator.withWriteLock {
                db.transaction {
                    db.pitscoutQueries.deletePitscoutByFormId(existing.formId)
                    persistTabInternal(cleared)
                }
            }
            cleared
        }
    }

    suspend fun saveDraft(tabId: String): PitScoutingTab? {
        return withContext(Dispatchers.IO) {
            val existing = getTabById(tabId) ?: return@withContext null
            val draft = existing.copy(
                isDraft = true,
                updatedAt = nowIso(),
                syncStatus = if (existing.isSubmitted) PitScoutingStatus.SUBMITTED else PitScoutingStatus.DRAFT
            )
            persistTab(draft)
            draft
        }
    }

    suspend fun deleteTab(tabId: String) {
        withContext(Dispatchers.IO) {
            val existing = getTabById(tabId)
            if (existing != null) {
                deleteLocalFiles(existing.images)
                LocalDatabaseWriteCoordinator.withWriteLock {
                    db.transaction {
                        db.pitscoutQueries.deletePitscoutByFormId(existing.formId)
                        db.pitScoutingTabQueries.deleteTab(tabId)
                    }
                }
            } else {
                LocalDatabaseWriteCoordinator.withWriteLock {
                    db.pitScoutingTabQueries.deleteTab(tabId)
                }
            }
        }
    }

    suspend fun deleteTabsForEvent(eventKey: String) {
        withContext(Dispatchers.IO) {
            val tabs = db.pitScoutingTabQueries.getAllTabsForEvent(eventKey)
                .executeAsList()
                .map { row -> rowToTab(row) }

            tabs.forEach { tab ->
                deleteLocalFiles(tab.images)
            }

            LocalDatabaseWriteCoordinator.withWriteLock {
                db.transaction {
                    tabs.forEach { tab ->
                        db.pitscoutQueries.deletePitscoutByFormId(tab.formId)
                    }
                    db.pitScoutingTabQueries.deleteTabsForEvent(eventKey)
                }
            }
        }
    }

    suspend fun addImages(tabId: String, bucket: String, uris: List<Uri>): PitScoutingTab? {
        return withContext(Dispatchers.IO) {
            val existing = getTabById(tabId) ?: return@withContext null
            val newAssets = uris.map { uri -> copyUriToLocalAsset(uri) }
            val updatedImages = when (bucket) {
                FULL_ROBOT_BUCKET -> existing.images.copy(fullRobotImages = existing.images.fullRobotImages + newAssets)
                DRIVE_TRAIN_BUCKET -> existing.images.copy(driveTrainImages = existing.images.driveTrainImages + newAssets)
                INTAKE_BUCKET -> existing.images.copy(intakeImages = existing.images.intakeImages + newAssets)
                else -> existing.images
            }
            val updatedTab = existing.asEdited(images = updatedImages)
            persistTab(updatedTab)
            updatedTab
        }
    }

    suspend fun removeImage(tabId: String, bucket: String, image: PitImageAsset): PitScoutingTab? {
        return withContext(Dispatchers.IO) {
            val existing = getTabById(tabId) ?: return@withContext null
            if (image.uploaded && image.id.isNotBlank() && networkMonitor.isConnected.value) {
                runCatching { surveyService.deletePitImage(image.id) }
            }
            image.localPath?.let { path -> runCatching { File(path).delete() } }
            val updatedImages = when (bucket) {
                FULL_ROBOT_BUCKET -> existing.images.copy(fullRobotImages = existing.images.fullRobotImages.filterNot { it.localPath == image.localPath && it.name == image.name })
                DRIVE_TRAIN_BUCKET -> existing.images.copy(driveTrainImages = existing.images.driveTrainImages.filterNot { it.localPath == image.localPath && it.name == image.name })
                INTAKE_BUCKET -> existing.images.copy(intakeImages = existing.images.intakeImages.filterNot { it.localPath == image.localPath && it.name == image.name })
                else -> existing.images
            }
            val updatedTab = existing.asEdited(images = updatedImages)
            persistTab(updatedTab)
            updatedTab
        }
    }

    suspend fun fetchAssignments(eventKey: String): List<PitAssignment> {
        return withContext(Dispatchers.IO) {
            runCatching {
                surveyService.getUserAssignments(formatEventId(eventKey)).data.orEmpty()
                    .mapNotNull { dto -> dto.toAssignmentOrNull() }
                    .filter { it.taskType == "pit-scouting" && it.assignedTeamNumbers.isNotEmpty() }
            }.getOrDefault(emptyList())
        }
    }

    suspend fun fetchCompletedTeams(eventKey: String): Set<String> {
        return withContext(Dispatchers.IO) {
            runCatching {
                surveyService.getEventTeams(formatEventId(eventKey)).data.orEmpty()
                    .filter { it.isPit == true }
                    .mapNotNull { it.teamNumber?.toString() }
                    .toSet()
            }.getOrDefault(emptySet())
        }
    }

    suspend fun getTeamSuggestions(query: String): List<TeamSuggestion> {
        if (query.isBlank()) {
            return emptyList()
        }

        return withContext(Dispatchers.IO) {
            val onlineResults = runCatching {
                if (networkMonitor.isConnected.value) {
                    surveyService.searchTeams(query).mapNotNull { dto -> dto.toTeamSuggestionOrNull() }
                } else {
                    emptyList()
                }
            }.getOrDefault(emptyList())

            if (onlineResults.isNotEmpty()) {
                onlineResults.distinctBy { it.teamNumber }
            } else {
                teamNameRepository.getAllTeams()
                    .filter {
                        it.team_number.contains(query, ignoreCase = true) ||
                            it.team_name.contains(query, ignoreCase = true)
                    }
                    .take(20)
                    .map { TeamSuggestion(it.team_number, it.team_name) }
            }
        }
    }

    suspend fun submitTab(tabId: String): Result<Boolean> {
        val tab = getTabById(tabId) ?: return Result.failure(IllegalArgumentException("Tab not found"))
        val userData = buildSubmissionUserData()
        val language = Locale.getDefault().toLanguageTag()
        val surveyData = buildSurveyData(tab.fields)

        return offlineSubmissionManager.submitOrQueue(
            isOnline = networkMonitor.isConnected.value,
            submitOnline = {
                val preparedTab = ensureUploadedImages(tab)
                val payload = buildSubmissionPayload(preparedTab, userData)
                val response = surveyService.submitSurveyResponse(payload)
                if (!response.success) {
                    Result.failure(IllegalStateException("Survey submission failed"))
                } else {
                    if (preparedTab.teamNumber.isNotBlank()) {
                        runCatching {
                            surveyService.updatePitStatus(
                                eventId = formatEventId(preparedTab.eventKey),
                                teamNumber = preparedTab.teamNumber,
                                request = com.team695.scoutifyapp.data.api.service.PitStatusUpdateRequest(true)
                            )
                        }
                    }

                    deleteLocalFiles(preparedTab.images)
                    val clearedTab = buildClearedTab(preparedTab, regenerateFormId = true)
                    runCatching {
                        LocalDatabaseWriteCoordinator.withWriteLock {
                            db.transaction {
                                db.pitscoutQueries.deletePitscoutByFormId(preparedTab.formId)
                                persistTabInternal(clearedTab)
                            }
                        }
                    }.onFailure { resetError ->
                        persistTab(
                            preparedTab.copy(
                                isDraft = false,
                                isSubmitted = true,
                                submissionTime = nowIso(),
                                updatedAt = nowIso(),
                                syncStatus = PitScoutingStatus.SUBMITTED,
                                lastError = "Submitted remotely, but local tab reset failed: ${resetError.message.orEmpty()}"
                            )
                        )
                    }
                    Result.success(Unit)
                }
            },
            queueOffline = {
                queueSubmission(tab, surveyData, buildUploadPayload(tab.images), userData, language)
            }
        )
    }

    override suspend fun push(): Result<Any> {
        return withContext(Dispatchers.IO) {
            val pendingTabs = db.pitScoutingTabQueries.getPendingSubmissionTabs().executeAsList().map { row -> rowToTab(row) }
            if (pendingTabs.isEmpty()) {
                return@withContext Result.success(0)
            }
            if (!networkMonitor.isConnected.value) {
                return@withContext Result.failure(IllegalStateException("Offline"))
            }

            var successCount = 0
            pendingTabs.forEach { queuedTab ->
                if (submitTab(queuedTab.tabId).getOrNull() == true) {
                    successCount += 1
                }
            }

            val remaining = db.pitScoutingTabQueries.getPendingSubmissionTabs().executeAsList().size
            if (remaining > 0) {
                Result.failure(IllegalStateException("Pending pit scouting submissions remain"))
            } else {
                Result.success(successCount)
            }
        }
    }

    private suspend fun queueSubmission(
        tab: PitScoutingTab,
        data: Map<String, Any?>,
        upload: Map<String, List<Map<String, Any?>>>,
        userData: SubmissionUserData,
        language: String
    ) {
        LocalDatabaseWriteCoordinator.withWriteLock {
            db.transaction {
                db.pitscoutQueries.deletePitscoutByFormId(tab.formId)
                db.pitscoutQueries.insertPitscout(
                    event_id = tab.eventKey,
                    form_id = tab.formId,
                    data_ = gson.toJson(data),
                    upload = gson.toJson(upload),
                    user_data = gson.toJson(userData),
                    user_agent = buildUserAgent(),
                    ip = null,
                    language = language
                )

                persistTabInternal(
                    tab.copy(
                        isDraft = false,
                        isSubmitted = false,
                        updatedAt = nowIso(),
                        syncStatus = PitScoutingStatus.PENDING_SUBMISSION,
                        lastError = null
                    )
                )
            }
        }
    }

    private suspend fun ensureUploadedImages(tab: PitScoutingTab): PitScoutingTab {
        if (!networkMonitor.isConnected.value) {
            return tab
        }

        val fullRobotImages = tab.images.fullRobotImages.map { image -> uploadImageIfNeeded(image, "fullRobot") }
        val driveTrainImages = tab.images.driveTrainImages.map { image -> uploadImageIfNeeded(image, "driveTrain") }
        val intakeImages = tab.images.intakeImages.map { image -> uploadImageIfNeeded(image, "intake") }
        val updatedTab = tab.copy(
            images = PitImageBundle(
                fullRobotImages = fullRobotImages,
                driveTrainImages = driveTrainImages,
                intakeImages = intakeImages
            ),
            updatedAt = nowIso()
        )
        persistTab(updatedTab)
        return updatedTab
    }

    private suspend fun uploadImageIfNeeded(image: PitImageAsset, apiType: String): PitImageAsset {
        if (image.uploaded && image.url.isNotBlank()) {
            return image
        }

        val localPath = image.localPath ?: return image
        val file = File(localPath)
        if (!file.exists()) {
            return image
        }

        val mimeType = image.mimeType ?: guessMimeType(image.name)
        val response = surveyService.uploadPitImage(
            file = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody(mimeType.toMediaTypeOrNull())),
            type = apiType.toRequestBody("text/plain".toMediaTypeOrNull())
        )
        return image.copy(id = response.id, url = response.url, uploaded = response.url.isNotBlank())
    }

    private fun buildSubmissionPayload(tab: PitScoutingTab, userData: SubmissionUserData): Map<String, Any?> {
        return mapOf(
            "eventId" to formatEventId(tab.eventKey),
            "tabs" to listOf(
                mapOf(
                    "name" to tab.tabName,
                    "formData" to buildProcessedFieldPayload(tab.fields),
                    "formId" to tab.formId
                )
            ),
            "images" to buildUploadPayload(tab.images),
            "deviceInfo" to mapOf(
                "userAgent" to buildUserAgent(),
                "ip" to null,
                "language" to Locale.getDefault().toLanguageTag()
            ),
            "userData" to userData,
            "user_data" to userData
        )
    }

    private fun buildProcessedFieldPayload(fields: List<PitFormField>): List<Map<String, Any?>> {
        return fields.sortedBy { it.originalIndex }.map { field ->
            buildMap {
                put("question", field.question)
                put("type", field.type.wireValue)
                put("required", field.required)
                put("value", normalizedFieldValue(field))
                put("originalIndex", field.originalIndex)
                if (!field.description.isNullOrBlank()) put("description", field.description)
                if (field.options.isNotEmpty()) put("options", field.options)
                if (field.optionValues.isNotEmpty()) put("optionValues", field.optionValues)
                if (field.otherValue.isNotBlank()) put("otherValue", field.otherValue)
            }
        }
    }

    private fun buildSurveyData(fields: List<PitFormField>): Map<String, Any?> {
        return fields.sortedBy { it.originalIndex }.associate { field -> field.question to normalizedFieldValue(field) }
    }

    private fun buildUploadPayload(images: PitImageBundle): Map<String, List<Map<String, Any?>>> {
        return mapOf(
            FULL_ROBOT_BUCKET to images.fullRobotImages.map { image -> buildUploadImagePayload(image) },
            DRIVE_TRAIN_BUCKET to images.driveTrainImages.map { image -> buildUploadImagePayload(image) },
            INTAKE_BUCKET to images.intakeImages.map { image -> buildUploadImagePayload(image) }
        )
    }

    private suspend fun buildSubmissionUserData(): SubmissionUserData {
        val user = userRepository.currentUser.first()
        val claims = decodeTokenClaims()
        val username = firstNonBlank(
            claims["preferred_username"]?.toString(),
            claims["username"]?.toString(),
            claims["name"]?.toString(),
            user?.name
        ).orEmpty()
        val displayName = firstNonBlank(
            claims["displayName"]?.toString(),
            claims["fullName"]?.toString(),
            claims["name"]?.toString(),
            user?.displayName,
            username
        ).orEmpty().ifBlank { "Unknown User" }
        val userId = firstNonBlank(
            claims["id"]?.toString(),
            claims["sub"]?.toString(),
            user?.androidID
        ).orEmpty()

        return SubmissionUserData(
            username = username,
            displayName = displayName,
            userId = userId,
            avatar = firstNonBlank(
                claims["avatar"]?.toString(),
                claims["picture"]?.toString()
            ).orEmpty(),
            email = firstNonBlank(
                claims["email"]?.toString(),
                user?.email
            ).orEmpty(),
            firstName = firstNonBlank(
                claims["firstName"]?.toString(),
                claims["given_name"]?.toString()
            ).orEmpty(),
            lastName = firstNonBlank(
                claims["lastName"]?.toString(),
                claims["family_name"]?.toString()
            ).orEmpty(),
            groups = normalizeClaimArray(claims["groups"]),
            roles = normalizeClaimArray(claims["roles"]),
            permissions = normalizeClaimArray(claims["permissions"])
        )
    }

    private fun normalizedFieldValue(field: PitFormField): Any? {
        return when (field.type) {
            FieldType.CHECKBOX -> {
                val currentValues = field.valueAsList()
                if (currentValues.contains("Other") && field.otherValue.isNotBlank()) {
                    currentValues.map { value -> if (value == "Other") field.otherValue.trim() else value }
                } else {
                    currentValues
                }
            }
            FieldType.NUMBER -> {
                val text = field.valueAsText().trim()
                if (text.isBlank()) null else text.toDoubleOrNull()?.let { if (it % 1.0 == 0.0) it.toInt() else it } ?: text
            }
            else -> {
                val text = field.valueAsText().trim()
                when {
                    text.isBlank() -> null
                    text == "Other" && field.otherValue.isNotBlank() -> field.otherValue.trim()
                    else -> text
                }
            }
        }
    }

    private suspend fun persistTab(tab: PitScoutingTab) {
        LocalDatabaseWriteCoordinator.withWriteLock {
            persistTabInternal(tab)
        }
    }

    private fun persistTabInternal(tab: PitScoutingTab) {
        db.pitScoutingTabQueries.insertTab(
            tabId = tab.tabId,
            teamNumber = tab.teamNumber,
            eventKey = tab.eventKey,
            formId = tab.formId,
            formVersion = tab.formVersion,
            fieldValuesJson = json.encodeToString(ListSerializer(PitFormField.serializer()), tab.fields),
            uploadDataJson = json.encodeToString(PitImageBundle.serializer(), tab.images),
            isDraft = if (tab.isDraft) 1L else 0L,
            isSubmitted = if (tab.isSubmitted) 1L else 0L,
            submissionTime = tab.submissionTime,
            createdAt = tab.createdAt,
            updatedAt = tab.updatedAt,
            syncStatus = tab.syncStatus.name
        )
    }

    private fun rowToTab(row: com.team695.scoutifyapp.db.PitscoutingTab): PitScoutingTab {
        val fields = runCatching { json.decodeFromString(ListSerializer(PitFormField.serializer()), row.fieldValuesJson) }
            .getOrElse { PitFormDataProvider.getDefaultFormFields(row.teamNumber) }
        val images = runCatching { row.uploadDataJson?.let { json.decodeFromString(PitImageBundle.serializer(), it) } }
            .getOrNull() ?: PitImageBundle()
        return PitScoutingTab(
            tabId = row.tabId,
            tabName = if (row.teamNumber.isBlank()) buildFallbackTabName(row.tabId) else buildTabName(row.teamNumber, 0),
            teamNumber = row.teamNumber,
            eventKey = row.eventKey,
            formId = row.formId,
            formVersion = row.formVersion,
            fields = fields,
            images = images,
            isDraft = row.isDraft == 1L,
            isSubmitted = row.isSubmitted == 1L,
            submissionTime = row.submissionTime,
            createdAt = row.createdAt,
            updatedAt = row.updatedAt,
            syncStatus = runCatching { PitScoutingStatus.valueOf(row.syncStatus) }.getOrDefault(PitScoutingStatus.DRAFT),
            lastError = null
        )
    }

    private fun PitScoutingTab.asEdited(fields: List<PitFormField> = this.fields, images: PitImageBundle = this.images): PitScoutingTab {
        val normalizedTeamNumber = normalizeTeamNumber(fields.firstOrNull { it.originalIndex == 0 }?.valueAsText().orEmpty())
        return copy(
            teamNumber = normalizedTeamNumber,
            tabName = if (normalizedTeamNumber.isBlank()) buildFallbackTabName(tabId) else buildTabName(normalizedTeamNumber, 0),
            fields = fields,
            images = images,
            isDraft = true,
            isSubmitted = false,
            submissionTime = null,
            updatedAt = nowIso(),
            syncStatus = PitScoutingStatus.DIRTY,
            lastError = null
        )
    }

    private fun buildClearedTab(existing: PitScoutingTab, regenerateFormId: Boolean): PitScoutingTab {
        return existing.copy(
            teamNumber = "",
            tabName = buildFallbackTabName(existing.tabId),
            formId = if (regenerateFormId) UUID.randomUUID().toString() else existing.formId,
            fields = PitFormDataProvider.createEmptyFormFields(),
            images = PitImageBundle(),
            isDraft = true,
            isSubmitted = false,
            submissionTime = null,
            createdAt = if (regenerateFormId) nowIso() else existing.createdAt,
            updatedAt = nowIso(),
            syncStatus = PitScoutingStatus.DRAFT,
            lastError = null
        )
    }

    private fun buildFallbackTabName(tabId: String): String = "Tab ${tabId.hashCode().absoluteValue % 900 + 100}"

    private fun buildTabName(teamNumber: String, fallbackIndex: Int): String {
        return if (teamNumber.isNotBlank()) "Team $teamNumber" else "Tab ${if (fallbackIndex <= 0) 1 else fallbackIndex}"
    }

    private fun normalizeTeamNumber(raw: String): String = raw.filter { it.isDigit() }

    private fun formatEventId(eventKey: String): String = eventKey.replace("_", "").lowercase()

    private fun nowIso(): String = Instant.now().toString()

    private fun buildUserAgent(): String = "Android/${android.os.Build.VERSION.RELEASE} (${android.os.Build.MODEL})"

    private fun buildUploadImagePayload(image: PitImageAsset): Map<String, Any?> {
        return mapOf(
            "id" to image.id.ifBlank { image.url },
            "url" to image.url,
            "name" to image.name,
            "size" to image.size
        )
    }

    private fun guessMimeType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', missingDelimiterValue = "jpg")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "image/jpeg"
    }

    private fun deleteLocalFiles(images: PitImageBundle) {
        (images.fullRobotImages + images.driveTrainImages + images.intakeImages).forEach { image ->
            image.localPath?.let { path -> runCatching { File(path).delete() } }
        }
    }

    private fun copyUriToLocalAsset(uri: Uri): PitImageAsset {
        val displayName = queryDisplayName(uri) ?: "pit-image-${UUID.randomUUID()}.jpg"
        val mimeType = context.contentResolver.getType(uri) ?: guessMimeType(displayName)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: displayName.substringAfterLast('.', missingDelimiterValue = "jpg")
        val targetDir = File(context.filesDir, "pit-images").apply { mkdirs() }
        val safeName = displayName.substringBeforeLast('.').replace(Regex("[^A-Za-z0-9_-]"), "_")
        val targetFile = File(targetDir, "${UUID.randomUUID()}-${safeName}.$extension")
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Unable to open the selected image.")

        try {
            inputStream.use { input ->
                targetFile.outputStream().use { output -> input.copyTo(output) }
            }
        } catch (error: Exception) {
            runCatching { targetFile.delete() }
            throw IllegalStateException("Failed to import the selected image.", error)
        }

        val fileSize = targetFile.length()
        if (fileSize <= 0L) {
            runCatching { targetFile.delete() }
            throw IllegalStateException("The selected image was empty.")
        }

        return PitImageAsset(name = displayName, size = fileSize, localPath = targetFile.absolutePath, mimeType = mimeType, uploaded = false)
    }

    private fun queryDisplayName(uri: Uri): String? {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        return context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val column = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (column >= 0) cursor.getString(column) else null
            } else {
                null
            }
        }
    }

    private fun PitAssignmentDto.toAssignmentOrNull(): PitAssignment? {
        val normalizedTaskType = taskType ?: return null
        return PitAssignment(id = id ?: UUID.randomUUID().toString(), taskType = normalizedTaskType, assignedTeamNumbers = assignedTeamNumbers.orEmpty().map { it.toString() })
    }

    private fun TeamSuggestionDto.toTeamSuggestionOrNull(): TeamSuggestion? {
        val normalizedNumber = teamNumber ?: teamNumberAlt ?: number ?: return null
        val normalizedName = teamName ?: teamNameAlt ?: name ?: nickname ?: ""
        return TeamSuggestion(normalizedNumber, normalizedName)
    }

    private fun CurrentEventResponse.toPitCurrentEvent(): PitCurrentEvent? {
        val normalizedEventId = eventId.trim()
        if (normalizedEventId.isBlank()) {
            return null
        }

        val normalizedName = eventName.trim()
        return PitCurrentEvent(
            eventId = normalizedEventId,
            eventDisplayName = if (normalizedName.isBlank()) {
                normalizedEventId
            } else {
                "$normalizedEventId | $normalizedName"
            }
        )
    }

    private suspend fun decodeTokenClaims(): Map<String, Any?> {
        val token = ScoutifyClient.tokenManager.getToken().orEmpty()
        val payload = token.split('.').getOrNull(1).orEmpty()
        if (payload.isBlank()) {
            return emptyMap()
        }

        return runCatching {
            val normalizedPayload = payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '=')
            val decodedPayload = String(Base64.decode(normalizedPayload, Base64.URL_SAFE or Base64.NO_WRAP))
            val type = object : TypeToken<Map<String, Any?>>() {}.type
            gson.fromJson<Map<String, Any?>>(decodedPayload, type) ?: emptyMap()
        }.getOrDefault(emptyMap())
    }

    private fun normalizeClaimArray(raw: Any?): List<String> {
        return when (raw) {
            is List<*> -> raw.mapNotNull { item -> item?.toString()?.trim() }.filter { it.isNotBlank() }
            is Array<*> -> raw.mapNotNull { item -> item?.toString()?.trim() }.filter { it.isNotBlank() }
            is String -> raw.split(',').map { it.trim() }.filter { it.isNotBlank() }
            else -> emptyList()
        }
    }

    private fun firstNonBlank(vararg values: String?): String? = values.firstOrNull { !it.isNullOrBlank() }

    companion object {
        const val FULL_ROBOT_BUCKET = "fullRobotImages"
        const val DRIVE_TRAIN_BUCKET = "driveTrainImages"
        const val INTAKE_BUCKET = "intakeImages"
    }
}
