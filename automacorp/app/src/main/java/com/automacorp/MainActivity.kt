package com.automacorp

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.automacorp.ui.theme.AutomacorpTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val ROOM_PARAM = "com.automacorp.room.attribute"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Button click lambda to navigate to RoomActivity
        val onSayHelloButtonClick: (name: String) -> Unit = { name ->
            val intent = Intent(this, RoomActivity::class.java).apply {
                putExtra(ROOM_PARAM, name) // Attach the room name to the Intent
            }
            startActivity(intent) // Start RoomActivity
        }

        // Navigate back lambda
        val navigateBack: () -> Unit = {
            startActivity(Intent(this, MainActivity::class.java)) // Navigates to MainActivity
        }

        setContent {
            AutomacorpTheme {
                Scaffold(

                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AutomacorpTopAppBar(
                            title = "Bienvenue !",
                            context = this, // Pass the Activity context
                            returnAction = { navigateBack() }
                        )
                    },
                    content = { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Greeting message or button action
                            Greeting(
                                name = "",
                                onClick = onSayHelloButtonClick,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}




@Composable
fun AppLogo(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = stringResource(R.string.app_logo_description),
        modifier = modifier
            .paddingFromBaseline(top = 100.dp)
            .height(80.dp)
    )
}

@Composable
fun Greeting(onClick: (name: String) -> Unit, modifier: Modifier = Modifier, name: String) {
    var nameState by remember { mutableStateOf(name) }

    Column(modifier = modifier) {
        AppLogo(Modifier.padding(top = 32.dp).fillMaxWidth())
        Text(
            text = stringResource(R.string.act_main_welcome),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = nameState,
            onValueChange = { nameState = it },
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            placeholder = {
                Row {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = stringResource(R.string.act_main_fill_name),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = stringResource(R.string.act_main_fill_name),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        )
        Button(
            onClick = { onClick(nameState) },
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.act_main_open))
        }
    }
}




