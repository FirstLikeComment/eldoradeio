package fr.isen.eldoradeio.profile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.activity_profil.*

private  var PICK_IMAGE = 1
class ProfileActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        firstnameProfile.setText("Nom: "+user?.email.toString())
        changeTitre()
        change_image.setOnClickListener {
            selectImage()
        }

        save.setOnClickListener {
            // update_processing.visibility = View.GONE
            //changeNom()
            CustomizedDialogBox().show(supportFragmentManager, "missiles")

        }

        profileFavoriteButton.setOnClickListener {
            val favoriteIntent = Intent(this@ProfileActivity,FavoriteActivity::class.java)
            startActivity(favoriteIntent)
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
        //update_processing.visibility = View.VISIBLE
        user?.let{
            firstnameProfile.setText(getString(R.string.firstname_update)+": "+user.email.toString())
        }
    }
}
