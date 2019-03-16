package fr.isen.eldoradeio.authentification

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var auth: FirebaseAuth
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mUserReference = mDatabase.getReference("users")
    private var spinnerItem: String = ""



    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(
            this@RegisterActivity,
            getString(R.string.hey_you_bream),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            spinnerItem = parent.getItemAtPosition(position) as String
            Toast.makeText(
                this@RegisterActivity,
                getString(R.string.toast_spinner_register) + spinnerItem,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        handleSpinner()

        auth = FirebaseAuth.getInstance()
        selectDate()

        bRegister.setOnClickListener {
            createAccount()
        }
    }

    data class User(
        var emailUser: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val dob: String = "",
        val year: String = "",
        var uid: String = ""
    )

    public fun createAccount() {
        val pwd: String = pwdFieldRegister.text.toString()
        val email: String = emailFieldRegister.text.toString()
        val firstname: String = firstNameFieldRegister.text.toString()
        val lastname: String = lastNameFieldRegister.text.toString()
        val dateOfBirth: String = dobFieldRegister.text.toString()

        if (email.isNotEmpty() && pwd.isNotEmpty() && firstname.isNotEmpty() && lastname.isNotEmpty() && dateOfBirth.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, "Sign up success", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        val userId = user!!.uid

                        val userToAdd = User(
                            email,
                            firstname,
                            lastname,
                            dateOfBirth,
                            spinnerItem,
                            userId
                        )
                        mUserReference.child(userId).setValue(userToAdd)

                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Authentification", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                }
        }
        else {
            Toast.makeText(baseContext, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            Log.w("SignIn", "signUpWithEmail:empty")
        }
    }

    private fun handleSpinner() {
        val spinner: Spinner = findViewById(R.id.year_spinner)
        ArrayAdapter.createFromResource(
            this@RegisterActivity,
            R.array.years_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(0, false)
            spinner.gravity = Gravity.CENTER
        }
        spinner.onItemSelectedListener = this
    }

    public fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            redirectToHome()
        }
    }

    private fun selectDate() {

        dobFieldRegister.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.clearFocus()
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val dateSave: String =
                            String.format(getString(R.string.date_format), year, monthOfYear + 1, dayOfMonth)
                        dobFieldRegister.setText(dateSave)
                    }, 1997, 0, 1
                )
                dpd.show()
            }
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        startActivity(intent)
    }

}
