package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.automacorp.model.RoomDto
import com.automacorp.service.ApiServices
import com.automacorp.service.RoomDetail
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response

class RoomListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: RoomViewModel by viewModels()


        // Define the back navigation function
        val navigateBack: () -> Unit = {
            startActivity(
                Intent(
                    baseContext,
                    MainActivity::class.java
                )
            ) // Navigate back to MainActivity
        }

        val openRoom:(Long) -> Unit = {
            val intent = Intent(this, RoomActivity::class.java).apply{
                putExtra(MainActivity.ROOM_PARAM,it.toString())
            }
            startActivity(intent)
        }
        // Launch coroutine to fetch the list of rooms
        lifecycleScope.launch(context = Dispatchers.IO) { // (1)
            runCatching {
                ApiServices.roomsApiService.findAll().execute() // Synchronous API call to fetch rooms
            }.onSuccess {
                val rooms: List<RoomDto> = it.body() ?: emptyList() // If body is null, use an empty list

                // Switch back to the Main thread to update the UI
                withContext(context = Dispatchers.Main) { // (2)
                    setContent {
                        val roomsState by viewModel.roomsState.asStateFlow().collectAsState() // (1)
                        LaunchedEffect(Unit) { // (2)
                            viewModel.findAll()
                        }
                        if (roomsState.error != null) {
                            setContent {
                                RoomList(emptyList(), navigateBack, openRoom)
                            }
                            Toast
                                .makeText(applicationContext, "Error on rooms loading ${roomsState.error}", Toast.LENGTH_LONG)
                                .show() // (3)
                        } else {
                            RoomList(roomsState.rooms, navigateBack, openRoom) // (4)
                        }
                    }
                }
            }.onFailure { exception ->
                // If the request fails, update UI on the main thread
                withContext(context = Dispatchers.Main) { // (2)
                    setContent {
                                RoomList(
                                    rooms = emptyList(),
                                    navigateBack = navigateBack,
                                    openRoom = openRoom
                                )
                            }
                    }

                    exception.printStackTrace() // Log the exception
                    Toast.makeText(this@RoomListActivity, "Error loading rooms: $exception", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    @Composable
    fun RoomListScreen(modifier: Modifier = Modifier, openRoom: (Long) -> Unit) {
        val rooms = RoomService.findAll() // Fetch the list of rooms

        LazyColumn(
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(rooms, key = { it.id }) { room ->
                RoomItem(
                    room = room,
                    modifier = Modifier.clickable { openRoom(room.id) }
                )
            }
        }
    }


    @Composable
    fun RoomItem(room: RoomDto, modifier: Modifier = Modifier) {
        androidx.compose.material3.Card(
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                com.automacorp.ui.theme.PurpleGrey80
            )
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = modifier.padding(20.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                androidx.compose.foundation.layout.Column {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "Target temperature : " + (room.targetTemperature?.toString()
                            ?: "?") + "°",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = (room.currentTemperature?.toString() ?: "?") + "°",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Right,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }



    @Composable
    fun RoomList(
        rooms: List<RoomDto>,
        navigateBack: () -> Unit,
        openRoom: (id: Long) -> Unit
    ) {
        val context = LocalContext.current
        AutomacorpTheme {
            Scaffold(
                topBar = { AutomacorpTopAppBar("Rooms", navigateBack, context = context) }
            ) { innerPadding ->
                if (rooms.isEmpty()) {
                    Text(
                        text = "No room found",
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        items(rooms, key = { it.id }) {
                            RoomItem(
                                room = it,
                                modifier = Modifier.clickable { openRoom(it.id) },
                            )
                        }
                    }
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun RoomItemPreview() {
        AutomacorpTheme {
            RoomItem(RoomService.ROOMS[0])
        }
    }

