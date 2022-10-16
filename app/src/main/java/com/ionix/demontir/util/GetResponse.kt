package com.ionix.demontir.util

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class GetResponse @Inject constructor(
    val connectivityCheck: ConnectivityCheck
) {
    inline fun <reified Y> getFirestoreResponse(
        crossinline block: suspend () -> Task<DocumentSnapshot>
    ): Flow<Resource<Y>?> = flow {
        emit(Resource.Loading())

        if (!connectivityCheck.isConnectedToInternet()) {
            emit(Resource.Error("Check your Connection"))
            return@flow
        }

        try {
            val response = block().await()
            val result = response.toObject(Y::class.java)
            emit(Resource.Success(result))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(e.message ?: "Error while connecting to server"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }
    inline fun <reified Y> getFirestoreListResponse(
        crossinline block: suspend () -> Task<QuerySnapshot>
    ): Flow<Resource<List<Y>>?> = flow {
        emit(Resource.Loading())

        if (!connectivityCheck.isConnectedToInternet()) {
            emit(Resource.Error("Check your Connection"))
            return@flow
        }

        try {
            val response = block().await()
            val result = response.documents.mapNotNull {
                it.toObject(Y::class.java)
            }
            emit(Resource.Success(result))
        } catch (e: FirebaseFirestoreException) {
            emit(Resource.Error(e.message ?: "Error while connecting to server"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong"))
        }
    }
}
