package com.vaibhavdhunde.android.practice.realtimelocation.firebase

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import timber.log.Timber

object FireAuth {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logoutUser() {
        auth.signOut()
    }

    @Suppress("unused")
    suspend fun registerUser(email: String, password: String): Status {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Status.SUCCESS
        } catch (e: FirebaseException) {
            Timber.e("Failed to register user due to: ${e.message}")
            Status.FAILURE
        }
    }

    @Suppress("unused")
    suspend fun loginUser(email: String, password: String): Status {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Status.SUCCESS
        } catch (e: FirebaseException) {
            Timber.e("Failed to authenticate user due to: ${e.message}")
            Status.FAILURE
        }
    }
}