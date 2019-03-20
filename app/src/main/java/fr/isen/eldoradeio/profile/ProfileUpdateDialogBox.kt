package fr.isen.eldoradeio.profile

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R

class ProfileUpdateDialogBox : DialogFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var dob: TextInputEditText
    private lateinit var spinner: Spinner
    private lateinit var spinnerItem: String

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // ...
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            spinnerItem = parent.getItemAtPosition(position) as String
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_update, null)
            firstName = dialogView.findViewById(R.id.firstnameUpdate)
            lastName = dialogView.findViewById(R.id.lastnameUpdate)
            dob = dialogView.findViewById(R.id.dobFieldUpdate)
            spinner = dialogView.findViewById(R.id.year_spinner_update)
            selectDate()
            handleSpinner(dialogView)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(
                    "MODIFIER"
                ) { _, _ ->

                    updateProfil()

                }
                .setNegativeButton(
                    "NON"
                ) { _, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun changeNom() {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/firstName")
        mCommentReference
            .setValue(firstName.text.toString()) { _, _ ->
            }

    }

    private fun changeLastNom() {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/lastName")
        mCommentReference
            .setValue(lastName.text.toString()) { _, _ ->
            }

    }

    private fun changeDob() {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/dob")
        mCommentReference
            .setValue(dob.text.toString()) { _, _ ->
            }

    }

    private fun changeYear() {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/year")
        mCommentReference
            .setValue(spinnerItem) { _, _ ->
            }

    }

    private fun handleSpinner(dialogView: View) {
        val spinner: Spinner = dialogView.findViewById(R.id.year_spinner_update)
        ArrayAdapter.createFromResource(
            activity,
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

    private fun selectDate() {

        dob.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.clearFocus()
                activity?.let {
                    val dpd = DatePickerDialog(
                        it,
                        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                            val dateSave: String =
                                String.format(getString(R.string.date_format), year, monthOfYear + 1, dayOfMonth)
                            dob.setText(dateSave)
                        }, 1997, 0, 1
                    )
                    dpd.show()
                }
            }
        }
    }


    private fun updateProfil() {
        try//if(firstName.text !=null && lastName.text !=null &&dob.text !=null && spinner !=null )
        {
            changeNom()
            changeLastNom()
            changeDob()
            changeYear()
        } catch (e: Exception) {
            Toast.makeText(activity, getString(R.string.null_field), Toast.LENGTH_SHORT).show()
        }
    }


}
