package com.ionix.demontir.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.ionix.demontir.data.datastore.DatastoreDataSource
import com.ionix.demontir.data.firebase.FirebaseDataSource
import com.ionix.demontir.data.repository.AppRepository
import com.ionix.demontir.util.AppGoogleSignIn
import com.ionix.demontir.util.ConnectivityCheck
import com.ionix.demontir.util.GetResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppGoogleSignIn(@ApplicationContext context: Context) = AppGoogleSignIn(context)

    @Provides
    @Singleton
    fun provideConnectivityChecker(@ApplicationContext context: Context) =
        ConnectivityCheck(context)

    @Provides
    @Singleton
    fun provideGetResponse(connectivityCheck: ConnectivityCheck) = GetResponse(connectivityCheck)

    @Provides
    @Singleton
    fun provideFirestore():FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = FirebaseDatabase.getInstance().reference

    @Provides
    @Singleton
    fun provideFirebaseDataSource(
        firestoreDb:FirebaseFirestore,
        fbDb: DatabaseReference,
        fbAuth: FirebaseAuth,
        googleSignIn: AppGoogleSignIn,
        getResponse: GetResponse
    ) = FirebaseDataSource(firestoreDb, fbDb, fbAuth, googleSignIn, getResponse)

    @Provides
    @Singleton
    fun provideDatastoreDataSource(@ApplicationContext context: Context) =
        DatastoreDataSource(context.dataStore)

    @Provides
    @Singleton
    fun provideAppRepository(
        firebaseDataSource: FirebaseDataSource,
        datastoreDataSource: DatastoreDataSource
    ) = AppRepository(firebaseDataSource, datastoreDataSource)
}