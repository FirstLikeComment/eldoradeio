package fr.isen.eldoradeio.profile

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.dialog_update.*

class CustomizedDialogBox : DialogFragment(), AdapterView.OnItemSelectedListener
{
    //lateinit var firstName: TextInputLayout
    //lateinit var lastName: TextInputLayout
    lateinit var dob: TextInputEditText
    private var spinnerItem: String = ""

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
            selectDate()
            handleSpinner()
           // firstName = dialogView.findViewById(R.id.firstname)
            //lastName = dialogView.findViewById(R.id.lastname)
            dob = dialogView.findViewById(R.id.dobFieldUpdate)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("MODIFIER"
                ) { dialog, id ->

                    UpdateProfil()

                }
                .setNegativeButton("NON"
                ) { dialog, id ->
                    getDialog().cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    public fun changeNom()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/firstName")
        mCommentReference
            .setValue(firstnameUpdate.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeLastNom()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/lastName")
        mCommentReference
            .setValue(lastnameUpdate.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeDob()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/dob")
        mCommentReference
            .setValue(dobFieldUpdate.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeYear()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/year")
        mCommentReference
            .setValue(yearFieldUpdate.text.toString()) { firebaseError, firebase ->
            }

    }

    private fun handleSpinner(){
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_update, null)
        val spinner: Spinner = dialogView.findViewById(R.id.year_spinner)
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
    private fun selectDate(){

        dob.setOnFocusChangeListener {view, hasFocus->
            if (hasFocus) {
                view.clearFocus()
                activity?.let {
                    val dpd = DatePickerDialog(it,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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


    public fun UpdateProfil()
    {
        changeNom()
        changeLastNom()
        changeDob()
        changeYear()
    }



}
