package fr.ISEN.eldoradeio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import fr.ISEN.eldoradeio.Profile.ProfileActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}
