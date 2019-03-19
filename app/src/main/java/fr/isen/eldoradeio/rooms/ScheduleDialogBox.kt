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
import kotlinx.android.synthetic.main.dialog_schedule.*

class ScheduleDialogBox : DialogFragment()
{
    lateinit var descriptif: TextInputEditText
    lateinit var debut: TextInputEditText
    lateinit var fin: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_update, null)
            descriptif = dialogView.findViewById(R.id.descriptifField)
            debut = dialogView.findViewById(R.id.debutField)
            fin = dialogView.findViewById(R.id.finField)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("AJOUTER"
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

    public fun changeDescriptif()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("booking/descriptif")
        mCommentReference
            .setValue(descriptif.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeDebut()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("booking/debut")
        mCommentReference
            .setValue(debut.text.toString()) { firebaseError, firebase ->
            }

    }

    public fun changeFin()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("booking/fin")
        mCommentReference
            .setValue(fin.text.toString()) { firebaseError, firebase ->
            }

    }




    public fun UpdateProfil()
    {
        try//if(firstName.text !=null && lastName.text !=null &&dob.text !=null && spinner !=null )
        {
            changeDescriptif()
            changeDebut()
            changeFin()

        }
        catch (e:Exception)
        {
            Toast.makeText(activity, "null field", Toast.LENGTH_SHORT).show()
        }
    }



}
