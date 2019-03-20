package fr.isen.eldoradeio.rooms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.R
import fr.isen.eldoradeio.Reservation


class CommentAdapter(private val context: Context, private val listComments: ArrayList<Reservation>) : BaseAdapter() {
    private val mDatabase = FirebaseDatabase.getInstance()
    private val mCommentReference = mDatabase.getReference("booking")
    private val mReference = mDatabase.getReference("users")



    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun getCount(): Int {
        return listComments.size
    }

    override fun getItem(position: Int): Any {
        return listComments.get(position)
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

        holder.tvUser!!.text = listComments.get(position).userUid
        holder.tvDescriptif!!.text = listComments.get(position).description
        holder.tvDebut!!.text = listComments.get(position).beginning
        holder.tvFin!!.text = listComments.get(position).end




        return convertView
    }

    private inner class ViewHolder {

        var tvUser: TextView? = null
        var tvDescriptif: TextView? = null
        var tvDebut: TextView? = null
        var tvFin: TextView? = null

    }


}