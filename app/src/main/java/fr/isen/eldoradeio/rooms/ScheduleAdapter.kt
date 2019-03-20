package fr.isen.eldoradeio.rooms

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.Reservation


class ScheduleAdapter(private val context: Context, private val listBookings: ArrayList<Reservation>) : BaseAdapter() {
    companion object {
        const val TAG = "ScheduleAdapter"
    }

    private val mDatabase = FirebaseDatabase.getInstance()
    private val mUserReference = mDatabase.getReference("users")


    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return listBookings.size
    }

    override fun getItem(position: Int): Any {
        return listBookings[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.schedule_item, null, true)


            holder.tvUser = convertView!!.findViewById(R.id.userField) as TextView
            holder.tvDescriptif = convertView.findViewById(R.id.descField) as TextView
            holder.tvDebut = convertView.findViewById(R.id.startField) as TextView
            holder.tvFin = convertView.findViewById(R.id.finishField) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        //fetching our user's full name to display it
        mUserReference.child(listBookings[position].userUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val map = dataSnapshot.value as HashMap<String, Any>
                    val fullName = map["firstName"] as String + " " + map["lastName"] as String
                    holder.tvUser!!.text = fullName
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "loadUser:onCancelled", databaseError.toException())
                }
            })
        holder.tvUser!!.text = listBookings[position].userUid
        holder.tvDescriptif!!.text = listBookings[position].description
        holder.tvDebut!!.text = listBookings[position].beginning
        holder.tvFin!!.text = listBookings[position].end




        return convertView
    }

    private inner class ViewHolder {

        var tvUser: TextView? = null
        var tvDescriptif: TextView? = null
        var tvDebut: TextView? = null
        var tvFin: TextView? = null

    }


}