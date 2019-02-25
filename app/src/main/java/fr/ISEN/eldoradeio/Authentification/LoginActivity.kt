package fr.ISEN.eldoradeio.Authentification

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import fr.ISEN.eldoradeio.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bLogin.setOnClickListener {
            redirectToHome()
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
    }
}
