package fr.isen.eldoradeio.profile

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.eldoradeio.Group
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.dialog_join_group.*

class JoinGroupDialogBox : DialogFragment(), AdapterView.OnItemSelectedListener
{

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mGroupReference = mDatabase.getReference("groups")
    private val groupList = arrayListOf<Group>()
    private var currentGroup: Group? = null
    lateinit var groupSpinner: Spinner

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedGroup = parent?.getItemAtPosition(position) as Group
        currentGroup = selectedGroup
    }

    lateinit var groupName: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_join_group, null)
            mGroupReference.orderByKey().addListenerForSingleValueEvent(groupListener)
            groupSpinner = dialogView.findViewById(R.id.groupeChoiceSpinner)
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton("MODIFIER"
                ) { dialog, id ->
                    chooseGroup()
                }
                .setNegativeButton("ANNULER"
                ) { dialog, id ->
                    getDialog().cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private val groupListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            groupList.clear()
            if(dataSnapshot.childrenCount.toInt() != 0) {
                for (item: DataSnapshot in dataSnapshot.children) {
                    val map = item.value as HashMap<String, Any>
                    val usersArrayList = arrayListOf<String>()
                    if(item.hasChild("users")) {
                        val users = map["users"] as HashMap<String, String>
                        usersArrayList.addAll(ArrayList(users.keys))
                    }
                    val group = Group(map["groupName"] as String, usersArrayList, map["uuid"] as String)
                    groupList.add(group)
                    val groupChoiceAdapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_item, groupList)
                    groupChoiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    with(groupSpinner)
                    {
                        adapter = groupChoiceAdapter
                        setSelection(-1,false)
                        onItemSelectedListener = this@JoinGroupDialogBox
                        gravity = android.view.Gravity.CENTER
                    }
                }
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("GroupActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun chooseGroup()
    {
        try
        {
            Log.w("JoinGroupDialogBox","current group is $currentGroup")
            if(currentGroup != null) {
                val mAuth = FirebaseAuth.getInstance()
                val userId = mAuth.currentUser!!.uid
                mGroupReference.child(currentGroup!!.uuid).child("users").child(userId).setValue(true)
            }
        }
        catch (e:Exception)
        {
            Toast.makeText(activity, "There was a problem while joining the group", Toast.LENGTH_SHORT).show()
            Log.e("JoinGroupDialogBox","Error: ",e)
        }
    }



}
