package com.team695.scoutifyapp.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Serializes all in-app database writes so concurrent repository updates
 * do not compete for the same SQLite write lock.
 */
object LocalDatabaseWriteCoordinator {
    private val writeMutex = Mutex()

    suspend fun <T> withWriteLock(block: suspend () -> T): T {
        return writeMutex.withLock {
            block()
        }
    }
}
