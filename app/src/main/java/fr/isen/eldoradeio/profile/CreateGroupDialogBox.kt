package fr.isen.eldoradeio.profile

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R

class CreateGroupDialogBox : DialogFragment() {
    private lateinit var groupName: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_create_group, null)
            groupName = dialogView.findViewById(R.id.groupName)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(
                    "MODIFIER"
                ) { _, _ ->

                    createGroup()

                }
                .setNegativeButton(
                    "ANNULER"
                ) { _, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    private fun createGroup() {
        try {
            val mDatabase = FirebaseDatabase.getInstance()
            val mAuth = FirebaseAuth.getInstance()
            val userId = mAuth.currentUser!!.uid
            val mGroupReference = mDatabase.getReference("groups")
            val key = mGroupReference.push().key
            mGroupReference.child(key!!).child("groupName").setValue(groupName.text.toString())
            mGroupReference.child(key).child("uuid").setValue(key)
            mGroupReference.child(key).child("users").child(userId).setValue(true)
        } catch (e: Exception) {
            Toast.makeText(activity, getString(R.string.create_group_failed), Toast.LENGTH_SHORT).show()
            Log.e("CreateGroupDialogBox", "Error: ", e)
        }
    }


}
