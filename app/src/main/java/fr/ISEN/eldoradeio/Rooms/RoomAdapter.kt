package fr.ISEN.eldoradeio.Rooms

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import fr.ISEN.eldoradeio.R
import kotlinx.android.synthetic.main.room_item_row.view.*

class RoomAdapter (private val items : ArrayList<RoomsActivity.Room>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    companion object{

        const val TAG = "RoomsAdapter"
        const val ROOM_FULLY_AVAILABLE = 0
        const val ROOM_PARTIALLY_AVAILABLE = 1
        const val ROOM_UNAVAILABLE = 2

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.room_item_row, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.roomName.text = items[position].roomName
        when(items[position].availability) {
            ROOM_FULLY_AVAILABLE -> {
                holder.roomStatus.setColorFilter(ContextCompat.getColor(context,
                    R.color.colorRoomAvailable))
            }
            ROOM_PARTIALLY_AVAILABLE -> {
                holder.roomStatus.setColorFilter(ContextCompat.getColor(context,
                    R.color.colorRoomPartiallyAvailable))
            }
            ROOM_UNAVAILABLE -> {
                holder.roomStatus.setColorFilter(ContextCompat.getColor(context,
                    R.color.colorRoomUnavailable))
            }
        }
        holder.bookingButton.setOnClickListener {
            val bookingIntent = Intent(context, BookingActivity::class.java)
            bookingIntent.putExtra("roomID",items[position].uuid)
            context.startActivity(bookingIntent)
        }
    }


}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextViews and the imageView that constitute a random user
    val roomName: TextView = view.roomItemRoomNameText
    val roomStatus: ImageView = view.roomItemStatus
    val bookingButton: Button = view.rommItemBookButton
}