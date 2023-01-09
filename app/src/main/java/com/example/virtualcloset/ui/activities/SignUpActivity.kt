package com.example.virtualcloset.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import com.example.virtualcloset.R
import com.example.virtualcloset.databinding.ActivitySignUpBinding
import com.example.virtualcloset.firestore.FirestoreClass
import com.example.virtualcloset.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
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
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    val signUp = findViewById<TextView>(R.id.btn_signup)
    signUp.setOnClickListener {
        signUpUser()
    }

}

private fun validateRegister() : Boolean {

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

    val name = binding.inputName.text.toString().trim{ it <= ' '}
    val email = binding.inputEmail.text.toString().trim{ it <= ' '}
    val pass =binding.inputPassword.text.toString().trim{ it <= ' '}

    if (validateRegister()) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            binding.inputName.text.toString().trim { it <= ' ' },
                            binding.inputEmail.text.toString().trim { it <= ' ' }
                        )

                        FirestoreClass().userSignUp(this@SignUpActivity, user)

                        //FirebaseAuth.getInstance().signOut()
                        //finish()

                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
            )
    }
}
fun userSignUpSuccessful() {
    Toast.makeText(
        this@SignUpActivity,
        resources.getString(R.string.register_success),
        Toast.LENGTH_LONG
    ).show()
}
}