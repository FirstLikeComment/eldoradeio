package fr.ISEN.eldoradeio.Authentification

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.ISEN.eldoradeio.Authentification.HomeActivity
import fr.ISEN.eldoradeio.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        bRegister.setOnClickListener {
            createAccount()
        }
    }

    public fun createAccount()
    {
        auth.createUserWithEmailAndPassword(emailField.text.toString(), pwdField.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Authentification", "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Authentification", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

            }
    }

    public fun updateUI(currentUser: FirebaseUser?)
    {
        //Toast.makeText(baseContext, "Coucou!!!",Toast.LENGTH_SHORT).show()
        if(currentUser != null) {
            redirectToHome()
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        startActivity(intent)
    }
}
