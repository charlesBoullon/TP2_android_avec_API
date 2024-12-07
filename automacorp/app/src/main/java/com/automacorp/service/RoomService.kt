package com.automacorp.service


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.automacorp.R
import com.automacorp.model.RoomDto
import com.automacorp.model.WindowDto
import com.automacorp.model.WindowStatus
import kotlin.math.round

object RoomService {
    val ROOM_KIND: List<String> = listOf("Room", "Meeting", "Laboratory", "Office", "Boardroom")
    val ROOM_NUMBER: List<Char> = ('A'..'Z').toList()
    val WINDOW_KIND: List<String> = listOf("Sliding", "Bay", "Casement", "Hung", "Fixed")

    fun generateWindow(id: Long, roomId: Long, roomName: String): WindowDto {
        return WindowDto(
            id = id,
            name = "${WINDOW_KIND.random()} Window $id",
            roomName = roomName,
            roomId = roomId,
            windowStatus = WindowStatus.values().random()
        )
    }

    fun generateRoom(id: Long): RoomDto {
        val roomName = "${ROOM_NUMBER.random()}$id ${ROOM_KIND.random()}"
        val windows = (1..(1..6).random()).map { generateWindow(it.toLong(), id, roomName) }

        return RoomDto(
            id = id,
            name = roomName,
            currentTemperature = (15..30).random().toDouble(),
            targetTemperature = (15..22).random().toDouble(),
            windows = windows
        )
    }

    // Create 50 rooms
    val ROOMS = (1..50).map { generateRoom(it.toLong()) }.toMutableList()

    fun findAll(): List<RoomDto> {
        // Retourne toutes les pièces triées par nom
        return ROOMS.sortedBy { it.name }
    }

    fun findById(id: Long): RoomDto? {
        // Retourne la pièce correspondant à l'ID ou null si elle n'existe pas
        return ROOMS.find { it.id == id }
    }

    fun findByName(name: String): RoomDto? {
        // Retourne la pièce correspondant au nom ou null si elle n'existe pas
        return ROOMS.find { it.name.equals(name, ignoreCase = true) }
    }

    fun updateRoom(id: Long, room: RoomDto): RoomDto? {
        // update an existing room with the given values
        val index = ROOMS.indexOfFirst { it.id == id }
        val updatedRoom = findById(id)?.copy(
            name = room.name,
            targetTemperature = room.targetTemperature,
            currentTemperature = room.currentTemperature
        ) ?: throw IllegalArgumentException()
        return ROOMS.set(index, updatedRoom)
    }

    fun findByNameOrId(nameOrId: String?): RoomDto? {
        if (nameOrId != null) {
            return if (nameOrId.isDigitsOnly()) {
                findById(nameOrId.toLong())
            } else {
                findByName(nameOrId)
            }
        }
        return null
    }
}
@Composable
fun NoRoom() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.act_room_none),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RoomDetail(roomDto: RoomDto, modifier: Modifier = Modifier) {
    var room by remember { mutableStateOf(roomDto) }

    Column (modifier = modifier.padding(16.dp)) {
        // Room name
        OutlinedTextField(
            value = room.name,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { room = room.copy(name = it) },
            placeholder = { Text(stringResource(R.string.act_room_name)) },
        )

        // Current temperature
        Text(
            text = stringResource(
                id = R.string.act_room_current_temperature,
                room.currentTemperature ?: 0.0 // Provide a default value if null
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        // Target temperature (Slider)
        Text(
            text = stringResource(R.string.act_room_target_temperature),
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = room.targetTemperature?.toFloat() ?: 18.0f,
            onValueChange = { room = room.copy(targetTemperature = it.toDouble()) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f
        )
        Text(
            text = stringResource(
                R.string.act_room_target_temperature,
                round((room.targetTemperature ?: 18.0) * 10) / 10
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
