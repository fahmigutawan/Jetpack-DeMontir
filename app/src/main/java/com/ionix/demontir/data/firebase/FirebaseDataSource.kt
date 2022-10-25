package com.ionix.demontir.data.firebase

import android.os.Handler
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.ionix.demontir.model.api.request.*
import com.ionix.demontir.model.api.response.*
import com.ionix.demontir.util.AppGoogleSignIn
import com.ionix.demontir.util.GetResponse
import com.ionix.demontir.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val firestoreDB: FirebaseFirestore,
    private val realtimeDB: DatabaseReference,
    private val auth: FirebaseAuth,
    private val googleSignIn: AppGoogleSignIn,
    private val getResponse: GetResponse
) {
    // Check if user is logged in
    fun isLoggedIn() = auth.currentUser != null

    // Register with Email & Password
    fun registerWithEmailPassword(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password)

    // Login with Google (1st STEP)
    fun loginWithGoogle(launcher: ActivityResultLauncher<IntentSenderRequest>) =
        googleSignIn.login(launcher)

    // Login With Token after Google SignIn flow (2nd STEP)
    fun loginWithCredential(credential: AuthCredential) = auth.signInWithCredential(credential)

    // Login With Email & Password
    fun loginWithEmailPassword(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

    // Logout
    fun logout() = auth.signOut()

    // GET uid
    fun getCurrentUid(): String? = auth.currentUser?.uid

    // GET user info by ID
    fun getUserInfoById(uid: String): Flow<Resource<UserInfoResponse>?> =
        getResponse.getFirestoreResponse {
            firestoreDB
                .collection("user")
                .document(uid)
                .get()
        }

    // Get Bengkel by bengkel_id
    fun getBengkelByBengkelId(bengkel_id: String): Flow<Resource<BengkelResponse>?> =
        getResponse.getFirestoreResponse {
            firestoreDB.collection("bengkel").document(bengkel_id).get()
        }

    // Get Bengkel Pictures LIST by bengkel_id
    fun getBengkelPicturesByBengkelId(bengkel_id: String): Flow<Resource<List<BengkelPicturesResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("bengkel_picture")
                .whereGreaterThanOrEqualTo("bengkel_id", bengkel_id)
                .whereLessThanOrEqualTo("bengkel_id", "$bengkel_id\uF7FF").get()
        }

    // Get Bengkel Categories LIST by bengkel_id
    fun getBengkelCategoriesIdByBengkelId(bengkel_id: String): Flow<Resource<List<BengkelCategoryResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("bengkel_category")
                .whereGreaterThanOrEqualTo("bengkel_id", bengkel_id)
                .whereLessThanOrEqualTo("bengkel_id", "$bengkel_id\uF7FF").get()
        }

    // Get Category Detail by category_id
    fun getCategoryDetailByCategoryId(category_id: String): Flow<Resource<CategoryResponse>?> =
        getResponse.getFirestoreResponse {
            firestoreDB
                .collection("bengkel_category_detail")
                .document(category_id)
                .get()
        }

    // Get Bengkel Product
    fun getBengkelProductsByBengkelId(bengkel_id: String): Flow<Resource<List<BengkelProductResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("bengkel_product")
                .whereGreaterThanOrEqualTo("bengkel_id", bengkel_id)
                .whereLessThanOrEqualTo("bengkel_id", "$bengkel_id\uF7FF").get()
        }

    // Get Bengkel Product with limit
    fun getBengkelProductsByBengkelId(
        bengkel_id: String,
        limit: Long
    ): Flow<Resource<List<BengkelProductResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("bengkel_product")
                .whereGreaterThanOrEqualTo("bengkel_id", bengkel_id)
                .whereLessThanOrEqualTo("bengkel_id", "$bengkel_id\uF7FF")
                .limit(limit)
                .get()
        }

    // GET orderdetail by order_id
    fun getOrderDetailByOrderId(order_id: String): Flow<Resource<OrderResponse>?> =
        getResponse.getFirestoreResponse {
            firestoreDB.collection("order").document(order_id).get()
        }

    // Get Order Info LIST by UserId
    fun getOrdersByUserId(user_id: String): Flow<Resource<List<OrderResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("order")
                .whereGreaterThanOrEqualTo("user_id", user_id)
                .whereLessThanOrEqualTo("user_id", "$user_id\uF7FF")
                .get()
        }

    // Get Ordered Product List by OrderId
    fun getOrderedProductsByOrderId(order_id: String): Flow<Resource<List<OrderProductsResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("order_products")
                .whereGreaterThanOrEqualTo("order_id", order_id)
                .whereLessThanOrEqualTo("order_id", "$order_id\uF7FF")
                .get()
        }

    // Get nearest Bengkel LIST by GREATER LONGITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByGreaterThanLongitudeAsModal(longitude: Double): Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereGreaterThanOrEqualTo("bengkel_long", (longitude - 30).toString())
                .get()
        }

    // Get nearest Bengkel LIST by LESS LONGITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByLessThanLongitudeAsModal(longitude: Double): Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereLessThanOrEqualTo("bengkel_long", (longitude + 30).toString())
                .get()
        }

    // Get nearest Bengkel LIST by GREATER LATITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByGreaterThanLatitudeAsModal(latitude: Double): Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereGreaterThanOrEqualTo("bengkel_lat", (latitude - 30).toString())
                .get()
        }

    // Get nearest Bengkel LIST by LESS LATITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByLessThanLatitudeAsModal(latitude: Double): Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereLessThanOrEqualTo("bengkel_lat", (latitude + 30).toString())
                .get()
        }

    // Save user info on database
    fun saveUserInfo(
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        profile_picture:String,
        name:String
    ) {
        val currUser = auth.currentUser
        val user = UserInfoRequest(
            uid = currUser?.uid ?: "",
            name = currUser?.displayName ?: name,
            email = currUser?.email ?: "",
            profile_picture = currUser?.photoUrl?.toString() ?: profile_picture,
            user_long = "0",
            user_lat = "0"
        )

        firestoreDB.collection("user")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailed() }
    }

    // Create new Order
    fun createNewOrder(
        total_price: String,
        user_long: String,
        user_lat: String,
        bengkel_id: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit,
        listOfProduct: List<OrderProductRequest>
    ) {
        val randomId = firestoreDB.collection("order").document().id
        val order_id = "order-$randomId"
        val listOfProductMapped = listOfProduct.map {
            CreateOrderProductRequest(
                order_id = order_id,
                product_id = it.product_id,
                quantity = 1,
                sub_total_price = it.sub_total_price
            )
        }

        val request = CreateOrderRequest(
            order_id = order_id,
            order_status = 1,
            user_id = auth.uid ?: "",
            total_price = total_price,
            bengkel_id = bengkel_id,
            user_lat = user_lat,
            user_long = user_long
        )

        Handler().postDelayed(
            {
                firestoreDB
                    .collection("order")
                    .document(order_id)
                    .set(request)
                    .addOnSuccessListener {
                        createNewOrderProduct(
                            listOfProductMapped,
                            onSuccess = {
                                onSuccess(order_id)
                            },
                            onFailed
                        )
                    }
                    .addOnFailureListener {
                        onFailed()
                    }
            },
            2000
        )
    }

    // Create new order product
    fun createNewOrderProduct(
        listOfProduct: List<CreateOrderProductRequest>,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        var uploadedCount = 0

        listOfProduct.forEachIndexed { index, createOrderProductRequest ->
            val randomId = firestoreDB.collection("order_products").document().id
            Log.e("$index", "$createOrderProductRequest")

            firestoreDB
                .collection("order_products")
                .document(randomId)
                .set(createOrderProductRequest)
                .addOnSuccessListener {
                    uploadedCount += 1

                    if (index == listOfProduct.size - 1) {
                        if (uploadedCount == listOfProduct.size) onSuccess()
                        else onFailed()
                    }
                }
                .addOnFailureListener {
                    onFailed()
                }
        }
    }

    // GET available chat channel
    fun getAvailableChatChannel(
        possibleChannel1: String,
        possibleChannel2: String,
        user_1: String,
        user_2: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        realtimeDB
            .child("chat")
            .get()
            .addOnSuccessListener {
                when {
                    it.child(possibleChannel1).exists() -> {
                        onSuccess(possibleChannel1)
                        return@addOnSuccessListener
                    }

                    it.child(possibleChannel2).exists() -> {
                        onSuccess(possibleChannel2)
                        return@addOnSuccessListener
                    }

                    else -> {
                        createNewChatChannel(
                            channel_id = possibleChannel1,
                            onSuccess = onSuccess,
                            onFailed = onFailed,
                            user_1 = user_1,
                            user_2 = user_2
                        )
                        return@addOnSuccessListener
                    }
                }
            }.addOnFailureListener {
                onFailed()
            }
    }

    // CREATE new chat channel
    fun createNewChatChannel(
        channel_id: String,
        user_1: String,
        user_2: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        realtimeDB
            .child("chat")
            .child(channel_id)
            .child("chat_data")
            .setValue(
                CreateNewChatRealtimeDbRequest(
                    channel_id = channel_id,
                    count = 0,
                    user_1 = user_1,
                    user_2 = user_2
                )
            )
            .addOnSuccessListener {
                firestoreDB
                    .collection("chat_room")
                    .document()
                    .set(
                        CreateNewChatDataFirestoreRequest(
                            channel_id = channel_id,
                            user_1 = user_1,
                            user_2 = user_2
                        )
                    )
                    .addOnSuccessListener { onSuccess(channel_id) }
                    .addOnFailureListener { onFailed() }
            }
            .addOnFailureListener { onFailed() }
    }

    // LISTEN to chat by channel_id
    fun listenChatByChannelId(
        channel_id: String,
        onDataChange: (DataSnapshot) -> Unit,
        onCancelled: () -> Unit
    ) {
        realtimeDB
            .child("chat")
            .child(channel_id)
            .child("chat_room")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onDataChange(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    onCancelled()
                }
            })
    }

    // SEND chat to realtime DB
    fun sendChat(
        channel_id: String,
        chat: String,
        sender: String,
        receiver: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        realtimeDB
            .child("chat")
            .child(channel_id)
            .child("chat_data")
            .child("count")
            .get()
            .addOnSuccessListener {
                val count = it.value as Long
                val chatRoomRef = realtimeDB
                    .child("chat")
                    .child(channel_id)
                    .child("chat_room")
                val randomId = chatRoomRef.push().key
                val chat_id = "$count-$randomId"
                val sendChatRequest = SendChatRequest(
                    chat_id = chat_id,
                    channel_id = channel_id,
                    sender = sender,
                    receiver = receiver,
                    chat = chat
                )

                chatRoomRef
                    .child(chat_id)
                    .setValue(sendChatRequest)
                    .addOnSuccessListener {
                        realtimeDB
                            .child("chat")
                            .child(channel_id)
                            .child("chat_data")
                            .child("count")
                            .setValue(count + 1)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener {
                                onFailed()
                            }
                    }
                    .addOnFailureListener {
                        onFailed()
                    }
            }
            .addOnFailureListener {
                onFailed()
            }
    }

    // GET list of chat room uid1 from firestore
    fun getListOfChatRoomUid1(uid:String): Flow<Resource<List<GetChatDataFromFirestoreResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("chat_room")
                .whereGreaterThanOrEqualTo("user_1", uid)
                .whereLessThanOrEqualTo("user_1", "$uid\uF7FF")
                .get()
        }

    // GET list of chat room uid2 from firestore
    fun getListOfChatRoomUid2(uid:String): Flow<Resource<List<GetChatDataFromFirestoreResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("chat_room")
                .whereGreaterThanOrEqualTo("user_2", uid)
                .whereLessThanOrEqualTo("user_2", "$uid\uF7FF")
                .get()
        }
}

