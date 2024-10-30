package com.varunkumar.tasks.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.varunkumar.tasks.models.UserData

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    user: UserData?
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = user?.profilePictureUrl,
            contentDescription = user?.username
        )
    }
}