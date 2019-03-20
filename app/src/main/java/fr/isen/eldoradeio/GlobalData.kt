package fr.isen.eldoradeio

const val ROOM_FULLY_AVAILABLE = 0
const val ROOM_PARTIALLY_AVAILABLE = 1
const val ROOM_UNAVAILABLE = 2

data class Notification(
    val reservationUid: String = "",
    val userUid: String = ""
)
data class Room(
    val roomName: String = "",
    val roomFloor: Int,
    val availability: Int = ROOM_FULLY_AVAILABLE,
    var uuid: String = ""
)

data class Group(
    val groupName: String = "",
    val users: ArrayList<String>,
    var uuid: String = ""
) {
    override fun toString(): String = groupName
}