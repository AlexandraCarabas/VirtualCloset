package com.example.virtualcloset

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.virtualcloset.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : BaseActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var firebaseAuth:FirebaseAuth
    //private var databaseReference: DatabaseReference? = null
    //private var database:FirebaseDatabase? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        databaseReference = Firebase.database.reference

        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

        val signUp = findViewById<TextView>(R.id.btn_signup)
        signUp.setOnClickListener {
            signUpUser()
        }

    }

    private fun validateRegister() : Boolean {

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        return when{
            TextUtils.isEmpty(binding.inputName.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_name_empty), true)
                false
            }

            TextUtils.isEmpty(binding.inputEmail.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_email_empty), true)
                false
            }

            TextUtils.isEmpty(binding.inputPassword.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_pass_empty), true)
                false
            }

            TextUtils.isEmpty(binding.confirmInputPassword.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_confirm_pass_empty), true)
                false
            }

            binding.inputPassword.text.toString().trim{ it <= ' '} != binding.confirmInputPassword.text.toString().trim{ it <= ' '} -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_pass_mismatch), true)
                false
            }
            else -> {
                showErrorSnackBar(resources.getString(R.string.register_successful), false)
                true
            }
        }
    }

    private fun signUpUser() {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = binding.inputName.text.toString().trim{ it <= ' '}
        val email = binding.inputEmail.text.toString().trim{ it <= ' '}
        val pass =binding.inputPassword.text.toString().trim{ it <= ' '}

        if(validateRegister()) {

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener (
                    OnCompleteListener <AuthResult>{ task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}", false)

                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
                )
        }
    }
}