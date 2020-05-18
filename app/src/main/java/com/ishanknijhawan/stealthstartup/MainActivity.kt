package com.ishanknijhawan.stealthstartup

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        ivDatePicker.setOnClickListener {
            datePickerFunction()
        }

        ivEdit.setOnClickListener {
            showInputDialog()
        }
    }

    private fun showInputDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Enter Profession")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            profInput = input.text.toString()
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
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
