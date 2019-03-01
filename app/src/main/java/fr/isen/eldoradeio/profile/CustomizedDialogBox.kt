package fr.isen.eldoradeio.profile

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.dialog_update.*

class CustomizedDialogBox : DialogFragment()
{
    lateinit var userName: EditText
    lateinit var lastName: EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_update, null)
            userName = dialogView.findViewById(R.id.username)
            lastName = dialogView.findViewById(R.id.lastname)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("MODIFIER"
                ) { dialog, id ->
                    changeNom()
                    changeLastNom()
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
            .setValue(userName.text.toString()) { firebaseError, firebase ->
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



}
