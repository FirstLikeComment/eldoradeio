package fr.isen.eldoradeio.authentification

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.Room
import fr.isen.eldoradeio.RoomAdapter
import fr.isen.eldoradeio.profile.FavoriteActivity
import fr.isen.eldoradeio.profile.ProfileActivity
import fr.isen.eldoradeio.rooms.CommentAdapter
import fr.isen.eldoradeio.rooms.Reservation
import fr.isen.eldoradeio.rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    var str: String = ""

    private val user = FirebaseAuth.getInstance().currentUser
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mFavoriteReference = mDatabase.getReference("users").child(user!!.uid).child("favorites")
    private val mRoomReference = mDatabase.getReference("rooms")
    private val mBookingReference = mDatabase.getReference("booking")
    private val favoriteRoomList = arrayListOf<Room>()
    private val favoriteList = arrayListOf<String>()
    private val roomListAdapter = RoomAdapter(favoriteRoomList, favoriteList, this)

    private var listBooking=arrayListOf<Reservation>()
    private var listFB=arrayListOf<Reservation>()
    private val adapter = CommentAdapter(this,listBooking)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        changeTitre()
        downloadPicture()

        profile.setOnClickListener {
            afficheProfil()
        }

        recherche.setOnClickListener {
            afficheRooms()
        }


        deconnexion.setOnClickListener {
            deconnexion()
        }
        mBookingReference.addValueEventListener(bookingListener)
        mFavoriteReference.addValueEventListener(favoriteListener)
    }

    private fun downloadPicture(){
        // Reference to an image file in Cloud Storage
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val pathReference = storageReference.child(user!!.uid + "/profilePicture")

        pathReference.downloadUrl.addOnSuccessListener {
            val imageView = findViewById<ImageView>(R.id.circularProfilePicture)
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.doradegrise)
                .error(R.drawable.doraderouge)
                .into(imageView)
        }.addOnFailureListener {
            Toast.makeText(this@HomeActivity, "Failed download", Toast.LENGTH_SHORT).show()
        }
    }

    public fun changeTitre() {
        user?.let {
            str = getString(R.string.welcome_home) + user.email.toString()
            bienvenue.text = str
        }
    }

    public fun afficheProfil() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    public fun afficheRooms() {
        startActivity(Intent(this, RoomsActivity::class.java))
        finish()
    }

    public fun afficheLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    public fun deconnexion() {
        FirebaseAuth.getInstance().signOut()
        afficheLogin()
    }


    var bookingListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("ReadBooking", "loadItem:onCancelled", databaseError.toException())
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
                   // if(booking.bookingDate == bookingDate && booking.roomUid == roomID) {
                        listBooking.add(booking)
                    //}
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private val favoriteListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            favoriteRoomList.clear()
            favoriteList.clear()
            val items = dataSnapshot.children.iterator()
            if (items.hasNext()) {

                //check if the collection has any to do items or not
                while (items.hasNext()) {
                    //get current item
                    val currentItem = items.next()
                    //get current data in a map
                    favoriteList.add(
                        currentItem.key as String)

                }


            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.w(FavoriteActivity.TAG, "loadItem:onCancelled", databaseError.toException())
        }
    }
    private fun checkList()
    {
        for(item in listBooking)
        {
            if(favoriteList.contains(item.roomUid))
                listFB.add(item)
        }
    }

    private fun fatigue()
    {
        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mGroupReference = mDatabase.getReference("notification")
        val key = mGroupReference.push().key
        mGroupReference.child(key!!).child("uidBooking").setValue("")
    }
}
