package fr.isen.eldoradeio.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.activity_profil.*
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ProfileActivity"
    }

    val user = FirebaseAuth.getInstance().currentUser
    lateinit var mStorage: FirebaseStorage

    private var filePath: Uri? = null

    val REQUEST_SELECT_IMAGE_IN_ALBUM = 10
    val REQUEST_TAKE_PHOTO = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        mStorage= FirebaseStorage.getInstance()

        printAll()
        downloadPicture()

        profileModifier.setOnClickListener {
            // update_processing.visibility = View.GONE
            //changeNom()
            ProfileUpdateDialogBox().show(supportFragmentManager, "missiles")
            printAll()

        }

        profileFavoriteButton.setOnClickListener {
            val favoriteIntent = Intent(this@ProfileActivity,FavoriteActivity::class.java)
            startActivity(favoriteIntent)
        }

        profileGroupsButton.setOnClickListener {
            val groupIntent = Intent (this@ProfileActivity,GroupActivity::class.java)
            startActivity(groupIntent)
        }

        circularProfile.setOnClickListener {
            alertDialogCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && data != null && data.data != null) {
                data.let {
                    filePath = it.data
                    Toast.makeText(this, "$filePath", Toast.LENGTH_LONG).show()
                    if (filePath != null) {
                        val stream = contentResolver.openInputStream(filePath!!)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        circularProfile.setImageBitmap(bitmap)
                    }
                }
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && data != null && data.extras != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            filePath = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Title", null))
            circularProfile.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(this, "$filePath", Toast.LENGTH_LONG).show()
        }
        uploadPicture()
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
            Toast.makeText(this@ProfileActivity, "Permission accepted. Thanks!", Toast.LENGTH_SHORT).show()
        } else {
            //Permission is granted
            handler()
            Toast.makeText(this@ProfileActivity, "Permissions have already been accepted!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                selectImageInAlbum()
                Log.d(TAG,"WRITE_EXTERNAL_STORAGE permission granted")
                Toast.makeText(this@ProfileActivity, "WRITE_EXTERNAL_STORAGE permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.err_perm_select_image), Toast.LENGTH_LONG).show()
            }
            return //just to exit if statements
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                takePhoto()
                Log.d(TAG,"CAMERA permission granted")
                Toast.makeText(this@ProfileActivity, "CAMERA permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.err_perm_camera), Toast.LENGTH_LONG).show()
            }
            return
        }

    }

    private fun uploadPicture(){

        val mPictureReference = mStorage.reference

        filePath.let {
            //amélioration: progressBar
            val pictureRef = mPictureReference.child(user!!.uid + "/profilePicture")
            // Get the data from an ImageView as bytes
            val bitmap = (circularProfile.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = pictureRef.putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Toast.makeText(this@ProfileActivity, "Failed upload", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                Toast.makeText(this@ProfileActivity, "Upload successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadPicture(){
        // Reference to an image file in Cloud Storage
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val pathReference = storageReference.child(user!!.uid + "/profilePicture")

        pathReference.downloadUrl.addOnSuccessListener {
            val imageView = findViewById<ImageView>(R.id.circularProfile)
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.doradegrise)
                .error(R.drawable.doraderouge)
                .into(imageView)
        }.addOnFailureListener {
            Toast.makeText(this@ProfileActivity, "Failed download", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    private fun takePhoto() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun alertDialogCamera(){
        val builder = AlertDialog.Builder(this@ProfileActivity)
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
            Toast.makeText(this@ProfileActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    public fun printFirstName()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/firstName")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                val str:String = getString(R.string.profile_fName_value)+value.toString()
                firstnameProfile.text = str
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    public fun printLastName()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/lastName")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                val str:String = getString(R.string.profile_lName_value)+value.toString()
                lastnameProfile.text = str
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }
    public fun printDob()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/dob")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                val str:String = getString(R.string.profile_DOB_value)+value.toString()
                dobProfile.text = str
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }
    public fun printYear()
    {
        //update_processing.visibility = View.VISIBLE

        val mDatabase = FirebaseDatabase.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser!!.uid
        val mCommentReference = mDatabase.getReference("users/$userId/year")

        // Read from the database
        mCommentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                val str:String = getString(R.string.profile_year_value)+value.toString()
                yearProfile.text = str
            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }
    public fun printAll()
    {
        printFirstName()
        printLastName()
        printDob()
        printYear()
    }



}
