package fr.isen.eldoradeio.rooms

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import fr.isen.eldoradeio.Room
import com.google.firebase.database.*
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.Reservation
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
            selectedDate = "$year-${month+1}-$dayOfMonth"
            val mSelectedRoomReservationReference = mDatabase.getReference("booking")
            var selectedRoomReservationListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val listBooking = fetchSelectedRoomTodayReservations(dataSnapshot)
                    when(listBooking.size)
                    {
                        0 ->
                        {
                            bookingAvailabilityStatusText.text = getString(R.string.booking_availability_status,getString(R.string.status_available))
                            bookingAvailabilityStatusImageView.setColorFilter(
                                ContextCompat.getColor(this@BookingActivity,
                                    R.color.colorRoomAvailable))
                        }
                        in 1..4 ->
                        {
                            bookingAvailabilityStatusText.text = getString(R.string.booking_availability_status,getString(R.string.status_partially_available))
                            bookingAvailabilityStatusImageView.setColorFilter(
                                ContextCompat.getColor(this@BookingActivity,
                                    R.color.colorRoomPartiallyAvailable))
                        }
                        else ->
                        {
                            bookingAvailabilityStatusText.text = getString(R.string.booking_availability_status,getString(R.string.status_unavailable))
                            bookingAvailabilityStatusImageView.setColorFilter(
                                ContextCompat.getColor(this@BookingActivity,
                                    R.color.colorRoomUnavailable))
                        }

                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Item failed, log a message
                    Log.w("ReadBooking", "loadItem:onCancelled", databaseError.toException())
                }
            }
            mSelectedRoomReservationReference.addValueEventListener(selectedRoomReservationListener)
            bookingButton.isEnabled = true
        }

        bookingButton.setOnClickListener {
            val scheduleIntent = Intent(this@BookingActivity, ScheduleActivity::class.java)
            scheduleIntent.putExtra("roomID",selectedRoomID)
            scheduleIntent.putExtra("bookingDate",selectedDate)
            startActivity(scheduleIntent)
        }
    }

    private fun fetchSelectedRoomTodayReservations(dataSnapshot: DataSnapshot): ArrayList<Reservation> {
        val items = dataSnapshot.children.iterator()
        val listBooking = arrayListOf<Reservation>()
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
                    if(booking.bookingDate == selectedDate && booking.roomUid == selectedRoomID) {
                        listBooking.add(booking)
                    }
                }
            }
        }
        return listBooking
    }
}
