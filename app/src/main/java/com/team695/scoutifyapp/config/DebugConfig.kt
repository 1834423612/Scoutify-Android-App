package com.team695.scoutifyapp.config

/**
 * Debug configuration for development
 * 
 * Set BYPASS_AUTH = true to disable authentication and access all screens
 * without logging in. This is useful for UI development and testing.
 * 
 * IMPORTANT: Must be set to false before releasing to production!
 */
object DebugConfig {
    /**
     * When true, bypasses all authentication checks
     * WARNING: Set to false before production release!
     */
    const val BYPASS_AUTH = false  // Change to false for production
    
    /**
     * Mock user used when BYPASS_AUTH is enabled
     */
    const val DEBUG_USER_NAME = "DEBUG_USER"
    const val DEBUG_USER_ID = "debug-user-id"
}
