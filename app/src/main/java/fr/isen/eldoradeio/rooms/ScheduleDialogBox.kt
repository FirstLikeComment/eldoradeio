package fr.isen.eldoradeio.rooms

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

class ScheduleDialogBox : DialogFragment() {
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

    private lateinit var description: TextInputEditText
    private lateinit var beginning: TextInputEditText
    private lateinit var end: TextInputEditText
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser!!.uid
    private val mBookingReference = mDatabase.getReference("booking")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_schedule, null)
            description = dialogView.findViewById(R.id.descField)
            beginning = dialogView.findViewById(R.id.startField)
            end = dialogView.findViewById(R.id.finishField)
            val roomID = arguments?.getString("roomID")!!
            val bookingDate = arguments?.getString("bookingDate")!!

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(
                    "AJOUTER"
                ) { _, _ ->

                    sendBooking(roomID, bookingDate)

                }
                .setNegativeButton(
                    "NON"
                ) { _, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun addDescription(key: String) {
        mBookingReference.child(key).child("description").setValue(description.text.toString())

    }

    private fun addBeginning(key: String) {
        mBookingReference.child(key).child("beginning").setValue(beginning.text.toString())

    }

    private fun addEnd(key: String) {
        mBookingReference.child(key).child("end").setValue(end.text.toString())
    }

    private fun sendBooking(roomID: String, bookingDate: String) {
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
        } catch (e: Exception) {
            Toast.makeText(activity, getString(R.string.book_room_failed), Toast.LENGTH_SHORT).show()
            Log.e("TAG", "bookingCreationError: ", e)
        }
    }


}
