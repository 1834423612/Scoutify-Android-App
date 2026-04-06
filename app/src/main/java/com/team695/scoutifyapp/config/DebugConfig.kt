package com.team695.scoutifyapp.config

import com.team695.scoutifyapp.BuildConfig

/**
 * Debug configuration for development
 *
 * Authentication bypass can only be enabled in debug builds.
 */
object DebugConfig {
    /**
     * Local-only override for debug builds.
     */
    private const val LOCAL_BYPASS_AUTH = false

    val BYPASS_AUTH: Boolean
        get() = BuildConfig.DEBUG && LOCAL_BYPASS_AUTH

    val ENABLE_LOCAL_DATABASE_DEBUGGING: Boolean
        get() = BuildConfig.DEBUG

    /**
     * Mock user used when BYPASS_AUTH is enabled
     */
    const val DEBUG_USER_NAME = "DEBUG_USER"
    const val DEBUG_USER_ID = "debug-user-id"
}
