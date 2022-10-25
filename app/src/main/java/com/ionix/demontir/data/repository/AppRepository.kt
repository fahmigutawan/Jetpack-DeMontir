package com.ionix.demontir.data.repository

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.firebase.auth.AuthCredential
import com.google.firebase.database.DataSnapshot
import com.ionix.demontir.data.datastore.DatastoreDataSource
import com.ionix.demontir.data.firebase.FirebaseDataSource
import com.ionix.demontir.model.api.request.OrderProductRequest
import com.ionix.demontir.model.api.response.BengkelResponse
import com.ionix.demontir.util.Resource
import kotlinx.coroutines.flow.Flow
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

    // GET Uid
    fun getCurrentUid() = firebaseDataSource.getCurrentUid()

    // GET userInfo by Id
    fun getUserInfoById(uid: String) = firebaseDataSource.getUserInfoById(uid)

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

    // GET bengkel products by bengkel id with limit
    fun getBengkelProductsByBengkelId(bengkel_id: String, limit: Long) =
        firebaseDataSource.getBengkelProductsByBengkelId(bengkel_id, limit)

    // SAVE user ingo into database
    fun saveUserInfo(
        onSuccess: () -> Unit, onFailed: () -> Unit, profile_picture: String, name: String
    ) = firebaseDataSource.saveUserInfo(onSuccess, onFailed, profile_picture, name)

    // CREATE new order
    fun createNewOrder(
        total_price: String,
        user_long: String,
        user_lat: String,
        bengkel_id: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit,
        listOfProduct: List<OrderProductRequest>
    ) = firebaseDataSource.createNewOrder(
        total_price,
        user_long,
        user_lat,
        bengkel_id,
        onSuccess,
        onFailed,
        listOfProduct
    )

    // GET orderdetail by order_id
    fun getOrderDetailByOrderId(order_id: String) =
        firebaseDataSource.getOrderDetailByOrderId(order_id)

    // GET available chat channel
    fun getAvailableChatChannel(
        possibleChannel1: String,
        possibleChannel2: String,
        user_1: String,
        user_2: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit
    ) = firebaseDataSource.getAvailableChatChannel(
        possibleChannel1,
        possibleChannel2,
        user_1,
        user_2,
        onSuccess,
        onFailed
    )

    // LISTEN to chat room
    fun listenChatByChannelId(
        channel_id: String,
        onDataChange: (DataSnapshot) -> Unit,
        onCancelled: () -> Unit
    ) = firebaseDataSource.listenChatByChannelId(channel_id, onDataChange, onCancelled)

    // SEND chat
    fun sendChat(
        channel_id: String,
        chat: String,
        sender: String,
        receiver: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) = firebaseDataSource.sendChat(channel_id, chat, sender, receiver, onSuccess, onFailed)

    // GET list of chat room from uid1 firestore
    fun getListOfChatRoomUid1(uid:String) = firebaseDataSource.getListOfChatRoomUid1(uid)

    // GET list of chat room from uid2 firestore
    fun getListOfChatRoomUid2(uid:String) = firebaseDataSource.getListOfChatRoomUid2(uid)
}