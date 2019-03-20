package fr.isen.eldoradeio.authentification

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
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
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream
import java.sql.Timestamp

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var auth: FirebaseAuth
    lateinit var mDatabase: FirebaseDatabase
    lateinit var mStorage:FirebaseStorage

    private var filePath: Uri? = null

    private var spinnerItem: String = ""

    val REQUEST_SELECT_IMAGE_IN_ALBUM = 10
    val REQUEST_TAKE_PHOTO = 20



    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(
            this@RegisterActivity,
            getString(R.string.hey_you_bream),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            spinnerItem = parent.getItemAtPosition(position) as String
            Toast.makeText(
                this@RegisterActivity,
                getString(R.string.toast_spinner_register) + spinnerItem,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        handleSpinner()

        auth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage= FirebaseStorage.getInstance()
        selectDate()

        bRegister.setOnClickListener {
            createAccount()
        }
        circularProfilePict.setOnClickListener {
            alertDialogCamera()
        }
    }

    data class User(
        var emailUser: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val dob: String = "",
        val year: String = "",
        var uid: String = ""
    )

    public fun createAccount() {

        val mUserReference = mDatabase.getReference("users")

        val pwd: String = pwdFieldRegister.text.toString()
        val email: String = emailFieldRegister.text.toString()
        val firstname: String = firstNameFieldRegister.text.toString()
        val lastname: String = lastNameFieldRegister.text.toString()
        val dateOfBirth: String = dobFieldRegister.text.toString()

        if (email.isNotEmpty() && pwd.isNotEmpty() && firstname.isNotEmpty() && lastname.isNotEmpty() && dateOfBirth.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Register success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, getString(R.string.singup_success), Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        val userId = user!!.uid

                        val userToAdd = User(
                            email,
                            firstname,
                            lastname,
                            dateOfBirth,
                            spinnerItem,
                            userId
                        )
                        mUserReference.child(userId).setValue(userToAdd)

                        updateUI(user)
                        uploadPicture()
                    } else {
                        // If register fails, display a message to the user.
                        Log.w("Authentification", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, getString(R.string.singup_failed), Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                }
        }
        else {
            Toast.makeText(baseContext, getString(R.string.missing_field), Toast.LENGTH_SHORT).show()
            Log.w("SignIn", "signUpWithEmail:empty")
        }
    }

    private fun handleSpinner() {
        val spinner: Spinner = findViewById(R.id.year_spinner)
        ArrayAdapter.createFromResource(
            this@RegisterActivity,
            R.array.years_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.setSelection(0, false)
            spinner.gravity = Gravity.CENTER
        }
        spinner.onItemSelectedListener = this
    }

    public fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            redirectToHome()
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
                        circularProfilePict.setImageBitmap(bitmap)
                    }
                }
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && data != null && data.extras != null) {
            val imageBitmap = data.extras?.get("data") as Bitmap
            filePath = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, imageBitmap, "Title", null))
            circularProfilePict.setImageBitmap(imageBitmap)
        } else {
            Toast.makeText(this, "$filePath", Toast.LENGTH_LONG).show()
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
            Toast.makeText(this@RegisterActivity, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
        } else {
            //Permission is granted
            handler()
            Toast.makeText(this@RegisterActivity, getString(R.string.permission_already_granted), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                selectImageInAlbum()
                Log.d("HomeActivity:","WRITE_EXTERNAL_STORAGE permission granted")
                Toast.makeText(this@RegisterActivity, getString(R.string.write_external_storage_permission_granted), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.err_perm_select_image), Toast.LENGTH_LONG).show()
            }
            return //just to exit if statements
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                takePhoto()
                Log.d("HomeActivity:",getString(R.string.camera_permission_granted))
                Toast.makeText(this@RegisterActivity, getString(R.string.camera_permission_granted), Toast.LENGTH_SHORT).show()
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
            val pictureRef = mPictureReference.child(auth.currentUser!!.uid + "/profilePicture")
            // Get the data from an ImageView as bytes
            val bitmap = (circularProfilePict.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = pictureRef.putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Toast.makeText(this@RegisterActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                Toast.makeText(this@RegisterActivity, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
            }
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
        val builder = AlertDialog.Builder(this@RegisterActivity)
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
            Toast.makeText(this@RegisterActivity, getString(R.string.take_photo_cancel), Toast.LENGTH_SHORT).show()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun selectDate() {

        dobFieldRegister.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.clearFocus()
                val dpd = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val dateSave: String =
                            String.format(getString(R.string.date_format), year, monthOfYear + 1, dayOfMonth)
                        dobFieldRegister.setText(dateSave)
                    }, 1997, 0, 1
                )
                dpd.show()
            }
        }
    }

    private fun redirectToHome() {
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        startActivity(intent)
    }

}
