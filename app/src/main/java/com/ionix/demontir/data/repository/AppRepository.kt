package com.ionix.demontir.data.repository

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.firebase.auth.AuthCredential
import com.ionix.demontir.data.datastore.DatastoreDataSource
import com.ionix.demontir.data.firebase.FirebaseDataSource
import com.ionix.demontir.model.api.response.BengkelResponse
import com.ionix.demontir.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val datastoreDataSource: DatastoreDataSource
) {
    // Check isLogged In
    fun isLoggedIn() = firebaseDataSource.isLoggedIn()

    // Register with Email & Password
    fun registerWithEmailPassword(email: String, password: String) =
        firebaseDataSource.registerWithEmailPassword(email, password)

    // Login with Google
    fun loginWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>) =
        firebaseDataSource.loginWithGoogle(launcher)

    // Login With Token after Google SignIn flow
    fun loginWithCredential(credential: AuthCredential) =
        firebaseDataSource.loginWithCredential(credential)

    // Login with email & password
    fun loginWithEmailPassword(email: String, password: String) =
        firebaseDataSource.loginWithEmailPassword(email, password)

    // Logout
    fun logout() = firebaseDataSource.logout()

    // SET State about Entering App First Time
    suspend fun saveIsEnteringAppFirstTime() = datastoreDataSource.saveIsEnteringAppFirstTime()

    // GET State about Entering App First Time
    fun isEnteringAppFirstTime() = datastoreDataSource.isEnteringAppFirstTime

    // GET single bengkel by bengkel_id
    fun getBengkelById(bengkel_id: String) = firebaseDataSource.getBengkelByBengkelId(bengkel_id)

    // GET nearest bengkel LIST by longitude and latitude
    fun getNearestBengkelByLonglat(
        longitude: Double, latitude: Double
    ): Flow<Resource<List<BengkelResponse>>> = flow<Resource<List<BengkelResponse>>> {
        emit(Resource.Loading())

        /**
         * Will get greater and less than on LONGITUDE.
         * Then filter the latitude manually by filter
         */

        firebaseDataSource.getNearestBengkelByLessThanLongitudeAsModal(longitude).collect {
            it?.let {
                when (it) {
                    is Resource.Error -> {
                        emit(Resource.Error(it.message ?: "Something went wrong"))
                    }
                    is Resource.Loading -> {
                        emit(Resource.Loading())
                    }
                    is Resource.Success -> {
                        it.data?.let { result ->
                            val filteredResult = result
                                .filter {
                                    // Filter less than target longitude + 30
                                    (it.bengkel_long ?: "0").toDouble() >= (longitude - 30)
                                }
                                .filter {
                                    // Filter greater than target latitude - 30
                                    (it.bengkel_lat ?: "0").toDouble() >= (latitude - 30)
                                }
                                .filter {
                                    // Filter less than target latitude + 30
                                    (it.bengkel_lat ?: "0").toDouble() <= (latitude + 30)
                                }

                            emit(Resource.Success(filteredResult))
                        }
                    }
                }
            }
        }
    }

    // GET bengkel pictures by bengkel_id
    fun getBengkelPicturesByBengkelId(bengkel_id: String) =
        firebaseDataSource.getBengkelPicturesByBengkelId(bengkel_id)

    // GET bengkel products by bengkel id
    fun getBengkelProductsByBengkelId(bengkel_id: String) =
        firebaseDataSource.getBengkelProductsByBengkelId(bengkel_id)
}