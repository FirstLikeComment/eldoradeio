package fr.isen.eldoradeio

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fr.isen.eldoradeio.profile.ProfileActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}
