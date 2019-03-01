package fr.ISEN.eldoradeio.Authentification

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import fr.ISEN.eldoradeio.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var auth: FirebaseAuth
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mUserReference = mDatabase.getReference("users")
    private var spinnerItem: String = ""

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            spinnerItem = parent.getItemAtPosition(position) as String
            Toast.makeText(this@RegisterActivity, getString(R.string.toast_spinner_register)+ spinnerItem, Toast.LENGTH_SHORT).show()
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
        var uid: String = "")

    public fun createAccount()
    {

        if (emailFieldRegister != null && pwdFieldRegister != null){
            auth.createUserWithEmailAndPassword(emailFieldRegister.text.toString(), pwdFieldRegister.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, "Sign in success",Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        val userId = user!!.uid

                        val userToAdd = User(
                            emailFieldRegister.text.toString(),
                            firstNameFieldRegister.text.toString(),
                            lastNameFieldRegister.text.toString(),
                            dobFieldRegister.text.toString(),
                            spinnerItem,
                            userId)
                        mUserReference.child(userId).setValue(userToAdd)

                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Authentification", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                }
        }
    }

    private fun handleSpinner(){
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

    public fun updateUI(currentUser: FirebaseUser?)
    {
        //Toast.makeText(baseContext, "Coucou!!!",Toast.LENGTH_SHORT).show()
        if(currentUser != null) {
            redirectToHome()
        }
    }

    private fun selectDate(){

        dobFieldRegister.setOnFocusChangeListener {view, hasFocus->
            if (hasFocus) {
                view.clearFocus()
                val dpd = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val dateSave: String = String.format(getString(R.string.date_format), year, monthOfYear+1, dayOfMonth)
                        dobFieldRegister.setText(dateSave)
                    }, 1997, 0, 1)
                dpd.show()
            }
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        startActivity(intent)
    }

}
