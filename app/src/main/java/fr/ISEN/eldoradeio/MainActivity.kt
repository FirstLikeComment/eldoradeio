package fr.ISEN.eldoradeio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mRoomReference = mDatabase.getReference("rooms")

    companion object{

        const val ROOM_FULLY_AVAILABLE = 0
        const val ROOM_PARTIALLY_AVAILABLE = 1
        const val ROOM_UNAVAILABLE = 2

    }


    data class Room(
        val roomName: String = "",
        val roomFloor: Int,
        val availability: Int = ROOM_FULLY_AVAILABLE,
        var uuid: String = "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testRoom = Room("501",5)
        /*val key = mRoomReference.push().key!!
        testRoom.uuid = key
        mRoomReference.child(key).setValue(testRoom)*/
    }
}
