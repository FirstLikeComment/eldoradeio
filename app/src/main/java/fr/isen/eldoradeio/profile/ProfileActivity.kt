package fr.isen.eldoradeio.profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.activity_profil.*

private  var PICK_IMAGE = 1
class ProfileActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)


        PrintAll()
        change_image.setOnClickListener {
            selectImage()
        }

        save.setOnClickListener {
            // update_processing.visibility = View.GONE
            //changeNom()
            CustomizedDialogBox().show(supportFragmentManager, "missiles")
            PrintAll()

        }

        profileFavoriteButton.setOnClickListener {
            val favoriteIntent = Intent(this@ProfileActivity,FavoriteActivity::class.java)
            startActivity(favoriteIntent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        profil_picture.setImageURI(data?.data)
    }

    fun selectImage()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)

    }

    public fun PrintFirstName()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/firstName")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                firstnameProfile.setText("Nom: "+value.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ProfileActivity", "Failed to read value.", error.toException())
            }

        })
    }

    public fun PrintLastName()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/lastName")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                firstnameProfile.setText("Nom: "+value.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ProfileActivity", "Failed to read value.", error.toException())
            }

        })
    }
    public fun PrintDob()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/dob")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                dobProfile.setText("D.O.B: "+value.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ProfileActivity", "Failed to read value.", error.toException())
            }

        })
    }
    public fun PrintYear()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/"+userId+"/year")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                yearProfile.setText("Year: "+value.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("ProfileActivity", "Failed to read value.", error.toException())
            }

        })
    }
    public fun PrintAll()
    {
        PrintFirstName()
        PrintLastName()
        PrintDob()
        PrintYear()
    }



}
