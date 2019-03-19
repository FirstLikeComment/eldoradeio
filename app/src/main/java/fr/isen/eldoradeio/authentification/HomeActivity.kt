package fr.isen.eldoradeio.authentification

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import fr.isen.eldoradeio.profile.ProfileActivity
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_home.*
import java.util.jar.Manifest

class HomeActivity : AppCompatActivity() {

    val REQUEST_CODE = 1
    val REQUEST_SELECT_IMAGE_IN_ALBUM = 10
    val REQUEST_TAKE_PHOTO = 20
    val user = FirebaseAuth.getInstance().currentUser
    var str: String = ""

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
        circularProfilePicture.setOnClickListener {
            alertDialogCamera()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM)
            data?.let {
                val uri = it.data
                if (uri != null) {
                    val stream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(stream)
                    circularProfilePicture.setImageBitmap(bitmap)
                }
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (data != null){
                if (data.extras != null) {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    circularProfilePicture.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    private fun requestPermission(perm: String, requestCode: Int, handler: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
                perm
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            //Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    perm)) {
            }
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(perm),
                    requestCode
                )
            Toast.makeText(this@HomeActivity, "Permission accepted. Thanks!", Toast.LENGTH_SHORT).show()
        } else {
            //Permission is granted
            handler()
            Toast.makeText(this@HomeActivity, "Permissions have already been accepted!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                selectImageInAlbum()
                Log.d("HomeActivity:","WRITE_EXTERNAL_STORAGE permission granted")
                Toast.makeText(this@HomeActivity, "WRITE_EXTERNAL_STORAGE permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.err_perm_select_image), Toast.LENGTH_LONG).show()
            }
            return //just to exit if statements
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                takePhoto()
                Log.d("HomeActivity:","CAMERA permission granted")
                Toast.makeText(this@HomeActivity, "CAMERA permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.err_perm_camera), Toast.LENGTH_LONG).show()
            }
            return
        }

    }

    private fun alertDialogCamera(){
        val builder = AlertDialog.Builder(this@HomeActivity)
        builder.setTitle("Choose your new face.")
        builder.setPositiveButton("CHOOSE FROM GALLERY"){_,_ ->
            requestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_SELECT_IMAGE_IN_ALBUM) {
                selectImageInAlbum()
            }
        }
        builder.setNegativeButton("TAKE PHOTO"){_,_ ->
            requestPermission(android.Manifest.permission.CAMERA, REQUEST_TAKE_PHOTO) {
                takePhoto()
            }
        }
        builder.setNeutralButton("CANCEL"){_,_ ->
            Toast.makeText(this@HomeActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    fun takePhoto() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    public fun changeTitre() {
        user?.let {
            str = getString(R.string.welcome_home) + user.email.toString()
            bienvenue.text = str
        }
    }

    public fun afficheProfil() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    public fun afficheRooms() {
        startActivity(Intent(this, RoomsActivity::class.java))
        finish()
    }

    public fun afficheLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    public fun deconnexion() {
        FirebaseAuth.getInstance().signOut()
        afficheLogin()
    }
}
