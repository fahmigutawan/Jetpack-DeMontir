package com.ionix.demontir.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DatastoreDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // State about Entering App First Time
    private val _isEnteringAppFirstTimePref = booleanPreferencesKey("is_enter_first_time")
    suspend fun saveIsEnteringAppFirstTime() {
        dataStore.edit { it[_isEnteringAppFirstTimePref] = false }
    }
    val isEnteringAppFirstTime = dataStore.data.map { it[_isEnteringAppFirstTimePref] ?: true }
}