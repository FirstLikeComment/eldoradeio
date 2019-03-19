package fr.isen.eldoradeio.profile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.eldoradeio.Group
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.rooms.RoomsActivity
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity() {

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mGroupReference = mDatabase.getReference("groups")
    private val groupList = arrayListOf<Group>()
    private val groupListAdapater = GroupAdapter(groupList, this)
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        groupCreateGroupButton.setOnClickListener {
            // update_processing.visibility = View.GONE
            //changeNom()
            CreateGroupDialogBox().show(supportFragmentManager, "createGroup")
        }
        mGroupReference.orderByKey().addValueEventListener(groupListener)
        groupRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        groupRecyclerView.adapter = groupListAdapater
    }

    private val groupListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            groupList.clear()
            if(dataSnapshot.childrenCount.toInt() != 0) {
                for (item: DataSnapshot in dataSnapshot.children) {
                    if(item.hasChild("users")) {
                        val map = item.value as HashMap<String, Any>
                        val usersArrayList = arrayListOf<String>()
                        val group = Group(map["groupName"] as String, usersArrayList, map["uuid"] as String)
                        val users = map["users"] as HashMap<String, String>
                        usersArrayList.addAll(ArrayList(users.keys))
                        if(usersArrayList.contains(user!!.uid)) {
                            groupList.add(group)
                            groupListAdapater.notifyDataSetChanged()
                        }
                    }
                }
                if (dataSnapshot.childrenCount.toInt() == 0) {
                    groupListAdapater.notifyDataSetChanged()
                }
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("GroupActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }
}
