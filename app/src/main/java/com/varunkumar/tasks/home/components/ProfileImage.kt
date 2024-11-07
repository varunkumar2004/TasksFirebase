package com.varunkumar.tasks.home.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.varunkumar.tasks.sign_in.UserData

@Composable
fun ProfileImage(
    user: UserData,
    size: Dp = 40.dp,
    onProfileClick: () -> Unit = {}
) {
    Log.d("user that is ", user.toString())

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable { onProfileClick() }
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = user.username?.first().toString(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}