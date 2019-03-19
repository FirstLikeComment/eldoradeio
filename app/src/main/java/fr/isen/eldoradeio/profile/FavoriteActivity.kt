package fr.isen.eldoradeio.profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.RoomAdapter
import fr.isen.eldoradeio.Room
import fr.isen.eldoradeio.rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity() {
    companion object {
        const val TAG = "FavoriteActivity"
    }

    private val user = FirebaseAuth.getInstance().currentUser
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mFavoriteReference = mDatabase.getReference("users").child(user!!.uid).child("favorites")
    private val mRoomReference = mDatabase.getReference("rooms")
    private val favoriteRoomList = arrayListOf<Room>()
    private val favoriteList = arrayListOf<String>()
    private val roomListAdapter = RoomAdapter(favoriteRoomList, favoriteList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        mFavoriteReference.orderByKey().addValueEventListener(favoriteListener)
        roomListRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        roomListRecyclerView.adapter = roomListAdapter
        favoriteAddRoomButton.setOnClickListener {
            startActivity(Intent(this, RoomsActivity::class.java))
        }
    }

    private val favoriteListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            favoriteRoomList.clear()
            favoriteList.clear()
            Log.w(TAG, "loading ${dataSnapshot.childrenCount} favorites: ")
            for(roomKey: DataSnapshot in dataSnapshot.children)
            {
                mRoomReference.child(roomKey.key!!).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val map = dataSnapshot.value as HashMap<String, Any>
                        val room = Room(map["roomName"] as String, (map["roomFloor"] as Long).toInt(), (map["availability"] as Long).toInt(), map["uuid"] as String)
                        favoriteRoomList.add(room)
                        favoriteList.add(roomKey.key!!)
                        roomListAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "loadItem:onCancelled", databaseError.toException())
                    }
                })
            }
            if(dataSnapshot.childrenCount.toInt() == 0) {
                roomListAdapter.notifyDataSetChanged()
            }
            Toast.makeText(this@FavoriteActivity,getString(R.string.toast_room_list_changed), Toast.LENGTH_SHORT).show()
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(TAG, "loadItem:onCancelled", databaseError.toException())
        }
    }
}
