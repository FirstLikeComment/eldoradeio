package fr.isen.eldoradeio.authentification

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.profile.ProfileActivity
import fr.isen.eldoradeio.rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    companion object {
        const val TAG = "HomeActivity"
    }

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        changeTitle()
        downloadPicture()

        homeProfile.setOnClickListener {
            redirectToProfile()
        }

        homeSearch.setOnClickListener {
            redirectToRooms()
        }


        homeDisconnection.setOnClickListener {
            disconnection()
        }

    }

    private fun downloadPicture() {
        // Reference to an image file in Cloud Storage
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val pathReference = storageReference.child(user!!.uid + "/profilePicture")

        pathReference.downloadUrl.addOnSuccessListener {
            val imageView = findViewById<CircularImageView>(R.id.circularProfile)
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.doradegrise)
                .error(R.drawable.doraderouge)
                .into(imageView)
        }.addOnFailureListener {
            Toast.makeText(this@HomeActivity, getString(R.string.download_failure), Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeTitle() {
        val mDatabase = FirebaseDatabase.getInstance()
        val userId = user!!.uid
        val mReference = mDatabase.getReference("users/$userId/firstName")
        user.let {
            mReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = dataSnapshot.getValue(String::class.java)
                    val str: String = getString(R.string.welcome_home) + value.toString()
                    homeWelcome.text = str
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
        }
    }

    private fun redirectToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    private fun redirectToRooms() {
        startActivity(Intent(this, RoomsActivity::class.java))
        finish()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun disconnection() {
        FirebaseAuth.getInstance().signOut()
        redirectToLogin()
    }
}
