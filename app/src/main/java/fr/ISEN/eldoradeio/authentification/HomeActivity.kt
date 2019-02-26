package fr.ISEN.eldoradeio.authentification

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import fr.ISEN.eldoradeio.Profile.ProfileActivity
import fr.ISEN.eldoradeio.R
import fr.ISEN.eldoradeio.Rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        changeTitre()

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

    public fun changeTitre()
    {
        user?.let{
            bienvenue.text="Bienvenue "+user.email.toString()
        }
    }

    public fun afficheProfil()
    {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    public fun afficheRooms()
    {
        startActivity(Intent(this, RoomsActivity::class.java))
        finish()
    }

    public fun afficheLogin()
    {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    public fun deconnexion()
    {
        FirebaseAuth.getInstance().signOut()
    }
}
