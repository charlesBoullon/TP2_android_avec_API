package com.automacorp.service

import com.automacorp.model.RoomDto

class RoomList(
    val rooms: List<RoomDto> = emptyList(),
    val error: String? = null
)