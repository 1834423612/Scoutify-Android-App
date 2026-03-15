package com.team695.scoutifyapp.utility

class SubmissionQueuedException(
    cause: Throwable
) : IllegalStateException(
    "Online submit failed. Saved offline and will retry automatically.",
    cause
)

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
            try {
                queueOffline()
                Result.failure(SubmissionQueuedException(error))
            } catch (queueError: Exception) {
                error.addSuppressed(queueError)
                Result.failure(error)
            }
        }
    }

    suspend fun syncPending(syncAction: suspend () -> Result<Int>): Result<Int> {
        return syncAction()
    }
}
