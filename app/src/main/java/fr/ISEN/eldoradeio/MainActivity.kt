package fr.ISEN.eldoradeio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import fr.ISEN.eldoradeio.Rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_main.*

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

        /*for (i in 1..5) {
            for (j in 1..10)
            {
                val roomToAdd = Room((100*i+j).toString(),i, ROOM_FULLY_AVAILABLE)
                val key = mRoomReference.push().key!!
                roomToAdd.uuid = key
                mRoomReference.child(key).setValue(roomToAdd)
            }
        }*/

        mainHelloText.setOnClickListener {
            val roomIntent = Intent(this, RoomsActivity::class.java)
            startActivity(roomIntent)
        }


    }
}
