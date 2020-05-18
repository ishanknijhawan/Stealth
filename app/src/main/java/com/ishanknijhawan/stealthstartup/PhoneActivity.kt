package com.ishanknijhawan.stealthstartup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_phone.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class PhoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)

        val actionBar = supportActionBar
        actionBar!!.title = "Verify Phone number"
        optLayout.visibility = View.GONE
        textInputPhone.requestFocus()
        val username = intent.getStringExtra("USERNAME")

        pinView.setTextSize(20)

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        pinView.setPinViewEventListener { pinview, fromUser ->
            Toast.makeText(this, pinview.value, Toast.LENGTH_SHORT).show()
        }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                val code = p0.smsCode
                pinView.value = code
                Toast.makeText(this@PhoneActivity, "Phone number verified", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@PhoneActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK).or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("USER", username)
                startActivity(intent)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@PhoneActivity, "Error: $p0", Toast.LENGTH_SHORT).show()
            }
        }

        btnEnterPhone.setOnClickListener {
            val phoneInput = textInputPhone.editText?.text.toString().trim()
            if (!validatePhone(phoneInput)){
                return@setOnClickListener
            }
            else {
                optLayout.visibility = View.VISIBLE
                pinView.requestFocus()
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91$phoneInput",
                    60,
                    TimeUnit.SECONDS,
                    this,
                    callbacks
                )
            }
        }
    }

    private fun validatePhone(phoneInput: String): Boolean {
        return when {
            phoneInput.isEmpty() -> {
                textInputPhone.error = "Phone number can't be empty"
                false
            }
            phoneInput.length < 10 -> {
                textInputPhone.error = "Invalid Phone number"
                false
            }
            else -> {
                textInputPhone.error = null
                true
            }
        }
    }
}

