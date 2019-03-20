package fr.isen.eldoradeio

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.eldoradeio.rooms.BookingActivity
import kotlinx.android.synthetic.main.room_item_row.view.*

class RoomAdapter (private val items : ArrayList<Room>, private val favorites : ArrayList<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.room_item_row,
                parent,
                false
            )
        )
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
        if(favorites.contains(items[position].uuid))
        {
            holder.itemFavourite.setColorFilter(ContextCompat.getColor(context,
                R.color.colorFavoriteEnabled))
            holder.itemFavourite.setOnClickListener {
                let {
                    toggleFavoriteState(true, items[position])
                }
            }
        }
        else
        {
            holder.itemFavourite.setColorFilter(ContextCompat.getColor(context,
                R.color.colorFavoriteDisabled))
            holder.itemFavourite.setOnClickListener {
                let {
                    toggleFavoriteState(false, items[position])
                }
            }
        }
    }

    private fun toggleFavoriteState(isFavorite: Boolean, room: Room)
    {
        val user = FirebaseAuth.getInstance().currentUser
        val mDatabase = FirebaseDatabase.getInstance()
        val mFavoriteReference = mDatabase.getReference("users").child(user!!.uid).child("favorites")
        if(isFavorite)
        {
            mFavoriteReference.child(room.uuid).removeValue()
        }
        else
        {
            mFavoriteReference.child(room.uuid).setValue(true)
        }
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextViews and the imageView that constitute a random user
    val roomName: TextView = view.groupItemGroupNameText
    val roomStatus: ImageView = view.roomItemStatus
    val bookingButton: Button = view.groupItemRemoveButton
    val itemFavourite: ImageView = view.roomItemFavourite
}