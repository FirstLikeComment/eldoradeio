package fr.isen.eldoradeio.rooms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.Reservation
import kotlinx.android.synthetic.main.activity_schedule.*

class ScheduleActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ScheduleActivity"
    }
    private var listBooking: ArrayList<Reservation> = ArrayList()
    private val adapter = CommentAdapter(this,listBooking)
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mBookingReference = mDatabase.getReference("booking")
    private var roomID = ""
    private var bookingDate = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        getBookingsFromFirebase()
        roomID = intent.getStringExtra("roomID")
        bookingDate = intent.getStringExtra("bookingDate")
        commentListView.adapter = adapter

        nv_creneau.setOnClickListener{
            addToSchedule()
        }
    }

    private fun getBookingsFromFirebase()
    {
        mBookingReference.orderByKey().addValueEventListener(bookingListener)
    }

    private var bookingListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w(TAG, "loadBooking:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        listBooking.clear()
        //Check if current database contains any collection
        if (items.hasNext()) {

            //check if the collection has any to do items or not
            while (items.hasNext()) {
                //get current item
                val currentItem = items.next()
                //get current data in a map
                val map = currentItem.value as HashMap<String, Any>
                //key will return Firebase ID
                if(
                    currentItem.hasChild("userUid")&&
                    currentItem.hasChild("beginning")&&
                    currentItem.hasChild("end")&&
                    currentItem.hasChild("roomUid")&&
                    currentItem.hasChild("description")&&
                    currentItem.hasChild("bookingDate")&&
                    currentItem.hasChild("uuid")
                        ) {

                    val booking = Reservation(
                        map["userUid"] as String,
                        map["beginning"] as String,
                        map["end"] as String,
                        map["roomUid"] as String,
                        map["description"] as String,
                        map["bookingDate"] as String,
                        map["uuid"] as String
                    )
                    if(booking.bookingDate == bookingDate && booking.roomUid == roomID) {
                        listBooking.add(booking)
                    }
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun addToSchedule()
    {
        val dialogBox = ScheduleDialogBox.newInstance(roomID,bookingDate)
        dialogBox.show(supportFragmentManager, "addToSchedule")
    }
}