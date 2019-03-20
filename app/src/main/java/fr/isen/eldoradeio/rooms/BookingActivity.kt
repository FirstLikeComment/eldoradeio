package fr.isen.eldoradeio.rooms

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fr.isen.eldoradeio.Room
import com.google.firebase.database.*
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.activity_booking.*

class BookingActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BookingActivity"
    }

    private var selectedRoomID = ""

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mRoomReference = mDatabase.getReference("rooms")
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        bookingButton.isEnabled = false
        selectedRoomID = intent.getStringExtra("roomID")!!
        val mSelectedRoomReference = mRoomReference.child(selectedRoomID)

        val roomListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.value as HashMap<String, Any>
                val selectedRoom = Room(map["roomName"] as String, (map["roomFloor"] as Long).toInt(), (map["availability"] as Long).toInt(), map["uuid"] as String)
                bookingRoomHeaderText.text = getString(R.string.booking_room_header,selectedRoom.roomName)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadRoom:onCancelled", databaseError.toException())
            }
        }
        mSelectedRoomReference.addListenerForSingleValueEvent(roomListener)

        bookingCalendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            // TODO: make sure to handle displaying the room's availability!!
            selectedDate = "$year-${month+1}-$dayOfMonth"
            bookingButton.isEnabled = true
        }

        bookingButton.setOnClickListener {
            val scheduleIntent = Intent(this@BookingActivity, ScheduleActivity::class.java)
            scheduleIntent.putExtra("roomID",selectedRoomID)
            scheduleIntent.putExtra("bookingDate",selectedDate)
            startActivity(scheduleIntent)
        }
    }
}
