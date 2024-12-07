package com.automacorp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto // Ensure this import is correct
import com.automacorp.model.WindowDto
import com.automacorp.service.ApiServices
import com.automacorp.service.RoomList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    var room by mutableStateOf<RoomDto?>(null)

    val roomsState = MutableStateFlow(RoomList())

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) { // (1)
            runCatching { ApiServices.roomsApiService.findAll().execute() }
                .onSuccess {
                    val rooms = it.body() ?: emptyList()
                    roomsState.value = RoomList(rooms) // (2)
                }
                .onFailure {
                    it.printStackTrace()
                    roomsState.value = RoomList(emptyList(), it.stackTraceToString() ) // (3)
                }
        }
    }

    fun findRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.getRoomById(id).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature ?.let { Math.round(it * 10) /10.0 },
            currentTemperature = roomDto.currentTemperature,
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun createRoom(newRoom: RoomDto) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.createRoom(newRoom).execute() }
                .onSuccess { findAll() } // Met à jour la liste des salles après la création
                .onFailure { it.printStackTrace() }
        }
    }

    fun deleteRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.deleteRoomById(id).execute() }
                .onSuccess { findAll() } // Met à jour la liste des salles après suppression
                .onFailure { it.printStackTrace() }
        }
    }

    val windowsState = MutableStateFlow<List<WindowDto>>(emptyList())

    fun listWindows(roomId: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.listWindows(roomId).execute() }
                .onSuccess { windowsState.value = it.body() ?: emptyList() }
                .onFailure { it.printStackTrace() }
        }
    }

    fun updateWindow(windowId: Long, windowDto: WindowDto) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateWindow(windowId, windowDto).execute() }
                .onFailure { it.printStackTrace() }
        }
    }

}

@Composable
fun WindowsList(viewModel: RoomViewModel, roomId: Long, modifier: Modifier = Modifier) {
    val windows by viewModel.windowsState.collectAsState()

    LaunchedEffect(roomId) {
        viewModel.listWindows(roomId)
    }

    Column (modifier = modifier) {
        Text("Windows", style = MaterialTheme.typography.titleLarge)

        windows.forEach { window ->
            Text("Window ID: ${window.id}, State: ${window.windowStatus}")
            Button (onClick = { /* Call viewModel.updateWindow with updated data */ }) {
                Text("Update")
            }
        }
    }
}



