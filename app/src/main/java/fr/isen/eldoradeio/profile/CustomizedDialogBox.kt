package fr.isen.eldoradeio.profile

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R

class CustomizedDialogBox : DialogFragment()
{
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var dob: EditText
    lateinit var year: EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_update, null)
            firstName = dialogView.findViewById(R.id.firstname)
            lastName = dialogView.findViewById(R.id.lastname)
            dob = dialogView.findViewById(R.id.dob)
            year = dialogView.findViewById(R.id.year)

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
            .setValue(firstName.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeLastNom()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/lastName")
        mCommentReference
            .setValue(lastName.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeDob()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/dob")
        mCommentReference
            .setValue(dob.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeYear()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/year")
        mCommentReference
            .setValue(year.text.toString()) { firebaseError, firebase ->
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
