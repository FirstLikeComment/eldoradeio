package fr.ISEN.eldoradeio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import fr.ISEN.eldoradeio.Authentification.HomeActivity
import fr.ISEN.eldoradeio.Rooms.RoomsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
