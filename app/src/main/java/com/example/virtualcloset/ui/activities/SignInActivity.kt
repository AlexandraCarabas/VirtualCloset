package com.example.virtualcloset.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivitySignInBinding
import com.example.virtualcloset.firestore.FirestoreClass
import com.example.virtualcloset.models.User
import com.example.virtualcloset.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewSignIn.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener{
            signInUser()
        }
    }

    private fun validateSignIn():Boolean {

        return when{

            TextUtils.isEmpty(binding.inputEmail.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email_empty), true)
                false
            }

            TextUtils.isEmpty(binding.inputPassword.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_pass_empty), true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun signInUser() {

        if(validateSignIn()) {
            val email = binding.inputEmail.text.toString().trim {it <= ' '}
            val password = binding.inputPassword.text.toString().trim {it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        FirestoreClass().getUserDetails(this@SignInActivity)
                    }else{
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userSignedInSuccess(user: User) {
        Log.i("Name: ", user.name)
        Log.i("Email: ", user.email)

        startActivity(Intent(this@SignInActivity, NavigationActivity::class.java))
        intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
        finish()
    }

    override fun onStart() {
        super.onStart()
    }
}