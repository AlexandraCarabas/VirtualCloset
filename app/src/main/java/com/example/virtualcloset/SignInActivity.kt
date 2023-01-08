package com.example.virtualcloset

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.virtualcloset.databinding.ActivitySignInBinding
import com.example.virtualcloset.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        if(currentUser!=null){
            val intent = Intent(this,NavigationActivity::class.java)
            startActivity(intent)
        }

        binding.textViewSignIn.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignin.setOnClickListener{
            signInUser()
        }
    }

    private fun validateSignIn():Boolean {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(validateSignIn()) {
            val email = binding.inputEmail.text.toString().trim {it <= ' '}
            val password = binding.inputPassword.text.toString().trim {it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        //TODO - Send user to Navigate Activity
                        showErrorSnackBar("You are signed in successfully", false)
                    }else{
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }


    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this,NavigationActivity::class.java)
            startActivity(intent)
        }
    }
}