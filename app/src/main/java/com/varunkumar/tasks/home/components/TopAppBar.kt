package com.varunkumar.tasks.home.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.varunkumar.tasks.sign_in.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onProfileClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Tasks")
        },
        actions = {
            IconButton(
                onClick = onProfileClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null
                )
            }
            
//            ProfileImage(
//                user = user,
//                onProfileClick = onProfileClick
//            )

//            Spacer(modifier = Modifier.width(10.dp))
        }
    )
}

