package com.ishanknijhawan.stealthstartup

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


const val CONTACTS_PERMISSION_CODE = 200
lateinit var contactsPermission: Array<String>

var dateTime = ""
var mYear: Int = 0
var mMonth: Int = 0
var mDay: Int = 0
var profInput = ""

class MainActivity : AppCompatActivity() {

    private val email = FirebaseAuth.getInstance().currentUser!!.email
    val database = FirebaseFirestore.getInstance().collection("Users").document(email!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.title = "Welcome"

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        contactsPermission = arrayOf(android.Manifest.permission.READ_CONTACTS)
        if (!checkContactsPermission()) {
            requestContactsPermission()
        }

        database.addSnapshotListener { documentSnapshot, e ->
            tvUsername.text = "Username: ${documentSnapshot!!["name"].toString()}"
            tvEmail.text = "Email: $email"
            tvPhone.text = "Phone number: ${documentSnapshot["phone"].toString()}"
            tvCalendar.text = "Date of Birth: ${documentSnapshot["dob"].toString()}"
            tvProf.text = "Profession: ${documentSnapshot["prof"].toString()}"
        }

        ivDatePicker.setOnClickListener {
            datePickerFunction()
        }

        ivEdit.setOnClickListener {
            showInputDialog()
        }

        btnLogOutMIA.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Sign Out")
            builder.setMessage("Do you want to sign out from Stealth ?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)


            builder.setPositiveButton("Sign out"){dialogInterface, i ->
                val gsoo = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient =
                    GoogleSignIn.getClient(this, gsoo)

                val auth = FirebaseAuth.getInstance()
                auth.signOut()
                googleSignInClient.signOut()

                val intent = Intent(this, FrontPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            builder.setNegativeButton("Cancel"){dialogInterface, i ->

            }

            builder.setCancelable(true)
            builder.show()
        }
    }

    private fun showInputDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Enter Profession")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.requestFocus()
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            profInput = input.text.toString()
            database.update("prof", profInput)
            tvProf.text = profInput
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun datePickerFunction() { // Get Current Date
        val c: Calendar = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                dateTime = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                tvCalendar.text = dateTime
                database.update("dob", dateTime)
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun requestContactsPermission() {
        ActivityCompat.requestPermissions(this, contactsPermission, CONTACTS_PERMISSION_CODE)
    }

    private fun checkContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val contactsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!contactsAccepted) {
                    tvPermission.text = "Contacts permission: Not granted"
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                } else {
                    tvPermission.text = "Contacts permission: Granted"
                    //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
