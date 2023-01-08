package com.example.virtualcloset.firestore

import android.util.Log
import com.example.virtualcloset.SignUpActivity
import com.example.virtualcloset.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun userSignUp(activity: SignUpActivity, userInfo: User) {

        mFirestore.collection("users")
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
}