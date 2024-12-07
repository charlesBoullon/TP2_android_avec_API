package com.automacorp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.automacorp.ui.theme.AutomacorpTheme
import androidx.compose.ui.res.painterResource


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomacorpTopAppBar(
    title: String? = null,
    returnAction: (() -> Unit)? = null, // Make this parameter nullable
    context: Context
) {
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            IconButton(onClick = {
                if (returnAction != null) {
                    returnAction()
                }
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    )

    val actions: @Composable RowScope.() -> Unit = {
        // Room List Action
        IconButton(onClick = {
            val intent = Intent(context, RoomListActivity::class.java)
            context.startActivity(intent)
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_action_rooms), // Ensure this resource exists
                contentDescription = stringResource(R.string.app_go_room_description) // Ensure this string is defined
            )
        }

        // Send Email Action
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto://charles.boullon@gmail.com"))
            context.startActivity(intent)
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_action_mail),
                contentDescription = stringResource(R.string.app_go_mail_description)
            )
        }

        // GitHub Action
        IconButton(onClick = {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/charlesBoullon/automacorp_TB3")
            )
            context.startActivity(intent) // Use context to start the activity
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_action_github), // Correct icon reference
                contentDescription = stringResource(R.string.app_go_github_description) // Ensure this is defined
            )
        }
    }

    // Top App Bar with title and actions
    if (title == null) {
        MediumTopAppBar(
            title = { Text("") },
            colors = colors,
            actions = actions
        )
    } else {
        MediumTopAppBar(
            title = { Text(title) },
            colors = colors,
            navigationIcon = {
                returnAction?.let { action ->
                    IconButton(onClick = action) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.app_go_back_description)
                        )
                    }
                }
            },
            actions = actions
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AutomacorpTopAppBarPreview() {
    val context = LocalContext.current
    AutomacorpTheme {
        AutomacorpTopAppBar(
            title = "Room",
            returnAction = { /* navigate back logic here */ },
            context = context
        )
    }
}

