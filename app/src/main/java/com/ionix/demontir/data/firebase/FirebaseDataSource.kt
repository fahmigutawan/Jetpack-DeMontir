package com.ionix.demontir.data.firebase

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
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
    fun getCategoryDetailByCategoryId(category_id:String): Flow<Resource<CategoryResponse>?> =
        getResponse.getFirestoreResponse {
            firestoreDB
                .collection("bengkel_category_detail")
                .document(category_id)
                .get()
        }

    // Get Bengkel Product
    fun getBengkelProductsByBengkelId(bengkel_id:String):Flow<Resource<List<BengkelProductResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("bengkel_product")
                .whereGreaterThanOrEqualTo("bengkel_id", bengkel_id)
                .whereLessThanOrEqualTo("bengkel_id", "$bengkel_id\uF7FF").get()
        }

    // Get Order Info LIST by UserId
    fun getOrdersByUserId(user_id:String): Flow<Resource<List<OrderResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("order")
                .whereGreaterThanOrEqualTo("user_id", user_id)
                .whereLessThanOrEqualTo("user_id", "$user_id\uF7FF")
                .get()
        }

    // Get Ordered Product List by OrderId
    fun getOrderedProductsByOrderId(order_id:String):Flow<Resource<List<OrderProductsResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB.collection("order_products")
                .whereGreaterThanOrEqualTo("order_id", order_id)
                .whereLessThanOrEqualTo("order_id", "$order_id\uF7FF")
                .get()
        }

    // Get nearest Bengkel LIST by GREATER LONGITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByGreaterThanLongitudeAsModal(longitude:Double):Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereGreaterThanOrEqualTo("bengkel_long", (longitude - 30).toString())
                .get()
        }

    // Get nearest Bengkel LIST by LESS LONGITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByLessThanLongitudeAsModal(longitude:Double):Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereLessThanOrEqualTo("bengkel_long", (longitude + 30).toString())
                .get()
        }

    // Get nearest Bengkel LIST by GREATER LATITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByGreaterThanLatitudeAsModal(latitude:Double):Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereGreaterThanOrEqualTo("bengkel_lat", (latitude - 30).toString())
                .get()
        }

    // Get nearest Bengkel LIST by LESS LATITUDE
    // As modal mean, choose one of these function then filter it manually on kotlin.
    // To prevent requesting onto firestore too much
    fun getNearestBengkelByLessThanLatitudeAsModal(latitude:Double):Flow<Resource<List<BengkelResponse>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDB
                .collection("bengkel")
                .whereLessThanOrEqualTo("bengkel_lat", (latitude + 30).toString())
                .get()
        }
}

