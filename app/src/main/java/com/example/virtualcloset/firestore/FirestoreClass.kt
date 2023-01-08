package com.example.virtualcloset.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.virtualcloset.SignInActivity
import com.example.virtualcloset.SignUpActivity
import com.example.virtualcloset.models.User
import com.example.virtualcloset.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun userSignUp(activity: SignUpActivity, userInfo: User) {

        mFirestore.collection(Constants.USERS)
        //Document ID for users fields. Here the document is the User ID.
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userSignUpSuccessful()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun getCurrentUserID():String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if(currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.VIRTUALCLOSET_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.SIGNED_IN_USERNAME,
                    "${user.name}"
                )
                editor.apply()

                when (activity) {
                    is SignInActivity -> {
                        activity.userSignedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is SignInActivity -> {

                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details",
                    e
                )
            }

    }
}