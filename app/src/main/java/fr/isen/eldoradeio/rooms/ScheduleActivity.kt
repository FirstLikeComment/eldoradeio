package fr.isen.eldoradeio.rooms

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.authentification.RegisterActivity
import fr.isen.eldoradeio.profile.ScheduleDialogBox
import kotlinx.android.synthetic.main.activity_schedule.*

data class Reservation(
    val userUid: String = "",
    val debut: String = "",
    val fin: String = "",
    val roomUid: String = "",
    var descriptif: String = "",
    var uuid: String = ""
)

class ScheduleActivity : AppCompatActivity() {
    var listComments: ArrayList<Reservation> = ArrayList()
    private val adapter = CommentAdapter(this,listComments)
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mCommentReference = mDatabase.getReference("booking")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        getCommentsFromFirebase()
        commentListView.adapter = adapter

        nv_creneau.setOnClickListener{
            addCreneau()
        }
    }

    public fun getCommentsFromFirebase()
    {
        mCommentReference.orderByKey().addValueEventListener(itemListener)
    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("ReadComment", "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        listComments.clear()
        //Check if current database contains any collection
        if (items.hasNext()) {

            //check if the collection has any to do items or not
            while (items.hasNext()) {
                //get current item
                val currentItem = items.next()
                //get current data in a map
                val map = currentItem.value as HashMap<String, Any>
                //key will return Firebase ID
                val comment = Reservation(map.get("userUid") as String, map.get("debut") as String, map.get("fin") as String, map.get("roomUid") as String,map.get("descriptif") as String,map.get("uuid") as String)
                listComments.add(comment)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun addCreneau()
    {
        ScheduleDialogBox().show(supportFragmentManager, "coucou")
    }
}