package fr.isen.eldoradeio.rooms

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R

class ScheduleDialogBox (): DialogFragment()
{
    companion object {

        const val TAG = "ScheduleDialogBox"

        fun newInstance(roomID: String, bookingDate: String): ScheduleDialogBox {
            val fragment = ScheduleDialogBox()

            val bundle = Bundle().apply {
                putString("roomID", roomID)
                putString("bookingDate", bookingDate)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
    lateinit var description: TextInputEditText
    lateinit var beginning: TextInputEditText
    lateinit var end: TextInputEditText
    val mDatabase = FirebaseDatabase.getInstance()
    val mAuth = FirebaseAuth.getInstance()
    val userId = mAuth.currentUser!!.uid
    val mBookingReference = mDatabase.getReference("booking")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_schedule, null)
            description = dialogView.findViewById(R.id.descriptifField)
            beginning = dialogView.findViewById(R.id.debutField)
            end = dialogView.findViewById(R.id.finField)
            val roomID = arguments?.getString("roomID")!!
            val bookingDate = arguments?.getString("bookingDate")!!

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("AJOUTER"
                ) { dialog, id ->

                    sendBooking(roomID, bookingDate)

                }
                .setNegativeButton("NON"
                ) { dialog, id ->
                    getDialog().cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    public fun addDescription(key: String)
    {
        mBookingReference.child(key).child("description").setValue(description.text.toString())

    }

    public fun addBeginning(key: String)
    {
        mBookingReference.child(key).child("beginning").setValue(beginning.text.toString())

    }

    public fun addEnd(key: String)
    {
        mBookingReference.child(key).child("end").setValue(end.text.toString())
    }

    public fun sendBooking(roomID: String, bookingDate: String)
    {
        try//if(firstName.text !=null && lastName.text !=null &&dob.text !=null && spinner !=null )
        {
            val key = mBookingReference.push().key
            mBookingReference.child(key!!).child("userUid").setValue(userId)
            mBookingReference.child(key).child("roomUid").setValue(roomID)
            mBookingReference.child(key).child("bookingDate").setValue(bookingDate)
            mBookingReference.child(key).child("uuid").setValue(key)
            addDescription(key)
            addBeginning(key)
            addEnd(key)
        }
        catch (e:Exception)
        {
            Toast.makeText(activity, "There was an issue while booking the room", Toast.LENGTH_SHORT).show()
            Log.e("TAG","bookingCreationError: ",e)
        }
    }



}
