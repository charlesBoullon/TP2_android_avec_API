package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.automacorp.model.RoomDto
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round



class RoomActivity : ComponentActivity() {
    private lateinit var viewModel: RoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize ViewModel
        viewModel = RoomViewModel()

        val param = intent.getStringExtra(MainActivity.ROOM_PARAM)
        val room = RoomService.findByNameOrId(param)

        // If room is found, update the ViewModel state
        viewModel.room = room

        // Define the save action
        val onRoomSave: () -> Unit = {
            if (viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                RoomService.updateRoom(roomDto.id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG)
                    .show()
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        val navigateBack: () -> Unit = {
            startActivity(
                Intent(
                    baseContext,
                    RoomListActivity::class.java
                )
            ) // Navigates back to MainActivity
        }


        setContent {
            AutomacorpTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        // Use the AutomacorpTopAppBar with a title and navigate back action
                        AutomacorpTopAppBar(
                            title = "Room Details",
                            context = this, // Pass the Activity's context
                            returnAction = navigateBack
                        )
                    },
                    floatingActionButton = {RoomUpdateButton(onRoomSave)},
                    content = { innerPadding ->
                        if (viewModel.room != null) {
                            // Pass the ViewModel to RoomDetail
                            RoomDetail(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                        } else {
                            NoRoom(Modifier.padding(innerPadding))

                        }
                    }
                )
            }
        }
    }


    @Composable
    fun NoRoom(modifier: Modifier = Modifier) {
        // Afficher un message si aucune salle n'est trouvée
        Column(modifier = modifier) {
            Text(
                text = stringResource(R.string.act_room_none),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    @Composable
    fun RoomDetail(viewModel: RoomViewModel, modifier: Modifier = Modifier) {
        val room = viewModel.room // Access room from ViewModel

        Column(modifier = modifier.padding(16.dp)) {
            // Room name
            OutlinedTextField(
                value = room?.name ?: "",
                onValueChange = { newName ->
                    viewModel.room = room?.copy(name = newName) // Update room name in ViewModel
                },
                label = { Text(text = stringResource(R.string.act_room_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Current temperature
            Text(
                text = stringResource(
                    id = R.string.act_room_current_temperature,
                    room?.currentTemperature ?: 0.0 // Fallback if room is null
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            // Target temperature (Slider)
            Text(
                text = stringResource(R.string.act_room_target_temperature),
                modifier = Modifier.padding(top = 16.dp)
            )
            Slider(
                value = room?.targetTemperature?.toFloat()
                    ?: 0f,  // Ensure default value if room is null
                onValueChange = { newTemp ->
                    viewModel.room =
                        room?.copy(targetTemperature = newTemp.toDouble()) // Update target temperature in ViewModel
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                steps = 0,
                valueRange = 10f..28f
            )

            // Display target temperature
            Text(text = (round((room?.targetTemperature ?: 18.0) * 10) / 10).toString())
        }
    }


    @Composable
    fun RoomUpdateButton(onClick: () -> Unit) {
        ExtendedFloatingActionButton(
            onClick = { onClick() },
            icon = {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = stringResource(R.string.act_room_save) // Ensure this string resource exists
                )
            },
            text = {
                Text(text = stringResource(R.string.act_room_save)) // Ensure this string resource exists
            }
        )
    }
}

