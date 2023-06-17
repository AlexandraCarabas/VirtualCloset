package com.example.virtualcloset.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.models.Outfit
import com.example.virtualcloset.models.User
import com.example.virtualcloset.ui.activities.*
import com.example.virtualcloset.ui.fragments.BaseFragment
import com.example.virtualcloset.ui.fragments.TwoPieceFragment
import com.example.virtualcloset.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

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

    fun addItemToDatabase(activity: AddItemActivity, itemInfo: Item) {

        var curentUserID : String = getCurrentUserID()

        val items : String = Constants.USERS+"/"+ curentUserID+"/"+Constants.ITEMS
        mFirestore.collection(items)
            .document(itemInfo.id)
            .set(itemInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.itemAddedSuccessfully()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while addind item.",
                    e
                )
            }

    }

    fun addOutfitToDatabase (activity: BaseFragment, outfitInfo: Outfit) {

        var curentUserID : String = getCurrentUserID()

        val outfits: String = Constants.USERS+"/"+curentUserID+"/"+Constants.OUTFITS
        mFirestore.collection(outfits)
            .document(outfitInfo.id)
            .set(outfitInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.outfitAddedSuccessfully()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding outfit!",
                    e
                )
            }
    }

    fun updateItemToDatabase(activity: Activity, itemID: String, itemHashMap: HashMap<String, Any>){
        var curentUserID : String = getCurrentUserID()

        val items : String = Constants.USERS+"/"+ curentUserID+"/"+Constants.ITEMS
        mFirestore.collection(items)
            .document(itemID)
            .update(itemHashMap)
            .addOnSuccessListener {
                when(activity) {
                    is DisplayItemActivity -> {
                        activity.itemUpdatedSuccessfully()
                    }
                }
            }
            .addOnFailureListener { e ->
//                when(activity) {
//                    is DisplayItemActivity -> {
//
//                    }
//                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error updating item info",
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
                editor.putString(
                    Constants.SIGNED_IN_UID,
                    "${user.id}"
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