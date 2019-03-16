package fr.isen.eldoradeio.authentification

import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.firebase.auth.FirebaseAuth
import fr.isen.eldoradeio.profile.ProfileActivity
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    var str:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        makeCircleImage(R.drawable.dorade)

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

    public fun makeCircleImage(draw: Int){
        val img = BitmapFactory.decodeResource(resources, draw)
        val roundImg = RoundedBitmapDrawableFactory.create(resources, img)

        //Radius ?
        //roundImg.cornerRadius = 25f
        //Round Image
        roundImg.isCircular = true
        profilePicture.setImageDrawable(roundImg)

    }

    public fun changeTitre()
    {
        user?.let{
            str = getString(R.string.welcome_home)+user.email.toString()
            bienvenue.text= str
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
        afficheLogin()
    }
}
