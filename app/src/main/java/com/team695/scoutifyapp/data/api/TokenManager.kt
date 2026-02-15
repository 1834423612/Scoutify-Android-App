package com.team695.scoutifyapp.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import android.util.Base64

// Create the DataStore extension property
private val Context.dataStore by preferencesDataStore("user_prefs")

class TokenManager(private val context: Context) {

    private val cryptoManager = CryptoManager()
    private val TOKEN_KEY = stringPreferencesKey("access_token")

    suspend fun saveToken(token: String) {
        val bytes = token.toByteArray()
        val outputStream = ByteArrayOutputStream()

        // Encrypt the data
        cryptoManager.encrypt(bytes, outputStream)
        val encryptedData = outputStream.toByteArray()

        // Encode to Base64 to store as String in DataStore
        val encryptedString = Base64.encodeToString(encryptedData, Base64.DEFAULT)

        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = encryptedString
        }
    }

    suspend fun getToken(): String? {
        val encryptedString = context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }.first() ?: return null

        return try {
            val encryptedBytes = Base64.decode(encryptedString, Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(encryptedBytes)

            // Decrypt
            val decryptedBytes = cryptoManager.decrypt(inputStream)
            String(decryptedBytes)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }
}