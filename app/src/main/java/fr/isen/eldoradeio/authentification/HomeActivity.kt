package fr.isen.eldoradeio.authentification

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.profile.ProfileActivity
import fr.isen.eldoradeio.rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    var str: String = ""

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
}
