package fr.ISEN.eldoradeio.Authentification

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.ISEN.eldoradeio.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()

        bLogin.setOnClickListener {
            signIn()
        }
        bRegister.setOnClickListener {
            redirectToHome()
        }
    }

    public override fun onStart()
    {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    public fun signIn()
    {
        auth.signInWithEmailAndPassword(emailField.text.toString(), pwdField.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Sign in success",Toast.LENGTH_SHORT).show()
                    Log.d("SignIn", "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignIn", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Sign in failed. You might not be registered.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    public fun updateUI(currentUser: FirebaseUser?)
    {
        if(currentUser != null) {
            redirectToHome()
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
    }
}
