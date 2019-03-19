package fr.isen.eldoradeio

    const val ROOM_FULLY_AVAILABLE = 0
    const val ROOM_PARTIALLY_AVAILABLE = 1
    const val ROOM_UNAVAILABLE = 2

    data class Room(
        val roomName: String = "",
        val roomFloor: Int,
        val availability: Int = ROOM_FULLY_AVAILABLE,
        var uuid: String = ""
    )