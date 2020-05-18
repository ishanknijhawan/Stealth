package com.ishanknijhawan.stealthstartup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val actionBar = supportActionBar

        val userMap = hashMapOf<String,String>()
        val database = FirebaseFirestore.getInstance().collection("Users")

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val code = intent.getStringExtra("CODE")

        if (code == "signup"){
            actionBar!!.title = "Create an Account!"
            textInputPassword.hint = "Create password"
            btnSignInSIA.text = "Create Account"
        }
        else {
            actionBar!!.title = "Login to Stealth"
            textInputPassword.hint = "Password"
            btnSignInSIA.text = "Log In"
        }

        btnSignInSIA.setOnClickListener {
            val emailInput = textInputEmail.editText?.text.toString().trim()
            val passwordInput = textInputPassword.editText?.text.toString().trim()
            val userInput = textInputUsername.editText?.text.toString().trim()

            when {
                !validateEmail(emailInput) or !validateUsername(userInput) or !validatePassword(passwordInput) -> return@setOnClickListener
                code == "signup" -> {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailInput, passwordInput)
                        .addOnSuccessListener {
                            userMap["name"] = userInput
                            userMap["phone"] = ""
                            userMap["dob"] = ""
                            userMap["prof"] = ""
                            database.document(emailInput).set(userMap)

                            val intent = Intent(this, PhoneActivity::class.java)
                            intent.putExtra("USERNAME", userInput)
                            intent.putExtra("EMAIL", emailInput)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                        }
                }
                else -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(emailInput, passwordInput)
                        .addOnSuccessListener {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            Toast.makeText(this, "Welcome back, $userInput", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }
    }

    private fun validateEmail(emailInput: String): Boolean {
        return if (emailInput.isEmpty()) {
            textInputEmail.error = "Email can't be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.error = "Invalid email address"
            false
        } else {
            textInputEmail.error = null
            true
        }
    }

    private fun validatePassword(passwordInput: String): Boolean {
        return when {
            passwordInput.isEmpty() -> {
                textInputPassword.error = "Password can't be empty"
                false
            }
            passwordInput.length < 8 -> {
                textInputPassword.error = "Password must be of atleast 8 characters"
                false
            }
            else -> {
                textInputPassword.error = null
                true
            }
        }
    }

    private fun validateUsername(userInput: String): Boolean {
        return when {
            userInput.isEmpty() -> {
                textInputUsername.error = "Field can't be empty"
                false
            }
            userInput.length > 20 -> {
                textInputUsername.error = "Username too long"
                false
            }
            else -> {
                textInputUsername.error = null
                true
            }
        }
    }
}
