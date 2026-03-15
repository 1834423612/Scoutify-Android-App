package com.team695.scoutifyapp.utility

class OfflineSubmissionManager {
    suspend fun submitOrQueue(
        isOnline: Boolean,
        submitOnline: suspend () -> Result<Unit>,
        queueOffline: suspend () -> Unit
    ): Result<Boolean> {
        return try {
            if (isOnline) {
                submitOnline().map { true }
            } else {
                queueOffline()
                Result.success(false)
            }
        } catch (error: Exception) {
            queueOffline()
            Result.success(false)
        }
    }

    suspend fun syncPending(syncAction: suspend () -> Result<Int>): Result<Int> {
        return syncAction()
    }
}
