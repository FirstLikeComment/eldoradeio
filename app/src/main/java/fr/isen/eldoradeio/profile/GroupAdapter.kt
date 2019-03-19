package fr.isen.eldoradeio.profile

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.Group
import fr.isen.eldoradeio.R
import kotlinx.android.synthetic.main.group_item_row.view.*

class GroupAdapter (private val items : ArrayList<Group>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.group_item_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.groupName.text = items[position].groupName
        holder.removeButton.setOnClickListener {
            removeUserFromGroup(items[position])
        }
    }

    fun removeUserFromGroup(group: Group)
    {
        val user = FirebaseAuth.getInstance().currentUser
        val mDatabase = FirebaseDatabase.getInstance()
        val mGroupReference = mDatabase.getReference("groups").child(group.uuid)
        mGroupReference.child("users").child(user!!.uid).removeValue()
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextViews and the imageView that constitute a random user
    val groupName: TextView = view.groupItemGroupNameText
    val removeButton: Button = view.groupItemRemoveButton
}