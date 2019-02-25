package fr.ISEN.eldoradeio.Profile

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import fr.ISEN.eldoradeio.R
import kotlinx.android.synthetic.main.activity_profil.*


private  var PICK_IMAGE = 1
class ProfileActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        changeTitre()
        profil_picture.setOnClickListener {
            selectImage()
            }

        name.setOnClickListener {
            changeNom()
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

    public fun changeTitre()
    {
        user?.let{
            name.setText("Nom: "+user.email.toString())
        }
    }

    public fun changeNom()
    {
        user?.updateEmail(name.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    update_successful.visibility= View.VISIBLE
                }
            }

    }
}
