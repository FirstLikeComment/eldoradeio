package fr.ISEN.eldoradeio.Rooms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.ISEN.eldoradeio.R
import kotlinx.android.synthetic.main.activity_rooms.*



class RoomsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    data class Room(
        val roomName: String = "",
        val roomFloor: Int,
        val availability: Int = ROOM_FULLY_AVAILABLE,
        var uuid: String = "")

    companion object{

        const val TAG = "RoomsActivity"
        const val ROOM_FULLY_AVAILABLE = 0
        const val ROOM_PARTIALLY_AVAILABLE = 1
        const val ROOM_UNAVAILABLE = 2

    }

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mRoomReference = mDatabase.getReference("rooms")
    private val roomList = arrayListOf<Room>()
    private val selectedRoomList = arrayListOf<Room>()
    private val roomListAdapter = RoomAdapter(selectedRoomList, this)
    private var currentFloor: Int? = null
    private var floorList = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)
        roomListRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        getRoomsFromFirebase()

        val filterTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(getString(R.string.room_filter_floor)))
        filterTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(roomFilterTypeSpinner)
        {
            adapter = filterTypeAdapter
            setSelection(0, false)
            onItemSelectedListener = this@RoomsActivity
            gravity = Gravity.CENTER
        }

        val filterChoiceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, floorList)
        filterChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(roomFilterChoiceSpinner)
        {
            adapter = filterChoiceAdapter
            setSelection(0, false)
            onItemSelectedListener = this@RoomsActivity
            gravity = Gravity.CENTER
        }

        roomListRecyclerView.adapter = roomListAdapter
    }

    private fun getRoomsFromFirebase() {
        mRoomReference.orderByKey().addValueEventListener(itemListener)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val id = parent?.id
        when(id)
        {
            R.id.roomFilterChoiceSpinner ->
            {
                val selectedFloor = (parent.getItemAtPosition(position) as String).toInt()
                currentFloor = selectedFloor
                selectRoomsinFloor(selectedFloor)
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    private fun selectRoomsinFloor(selectedFloor: Int?) {
        if (selectedFloor != null)
        {
            selectedRoomList.clear()
            for (room: Room in roomList) {
                if (room.roomFloor == selectedFloor) selectedRoomList.add(room)
            }
            roomListAdapter.notifyDataSetChanged()
        }
    }

    private val itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            updateRoomList(dataSnapshot)
            Toast.makeText(this@RoomsActivity,getString(R.string.toast_room_list_changed),Toast.LENGTH_SHORT).show()
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun updateRoomList(dataSnapshot: DataSnapshot) {
        roomList.clear()
        floorList.clear()

        val items = dataSnapshot.children.iterator()
        if (items.hasNext()) {

            //check if the collection has any to do items or not
            while (items.hasNext()) {
                //get current item
                val currentItem = items.next()
                //get current data in a map
                val map = currentItem.value as HashMap<String, Any>
                val room = Room(map["roomName"] as String, (map["roomFloor"] as Long).toInt(), (map["availability"] as Long).toInt(), map["uuid"] as String)
                if (!floorList.contains(room.roomFloor.toString()))
                {
                    floorList.add(room.roomFloor.toString())
                }
                roomList.add(room)
            }
            floorList.sort()
            val filterChoiceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, floorList)
            filterChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            with(roomFilterChoiceSpinner)
            {
                adapter = filterChoiceAdapter
                setSelection(-1,false)
                onItemSelectedListener = this@RoomsActivity
                gravity = Gravity.CENTER
            }
        }
        selectRoomsinFloor(currentFloor)

    }
}
