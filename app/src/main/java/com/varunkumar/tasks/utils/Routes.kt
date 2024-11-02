package com.varunkumar.tasks.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.varunkumar.tasks.models.UserData
import kotlinx.serialization.Serializable

interface ScreenRoute {
    val route: String
    val title: String
    val outlinedIcon: ImageVector
    val filledIcon: ImageVector
}

@Serializable
object HomeScreenRoute

@Serializable
object SignInScreen : ScreenRoute {
    override val route: String = "sign_in_screen"
    override val title: String = "Profile"
    override val outlinedIcon: ImageVector = Icons.Outlined.Home
    override val filledIcon: ImageVector = Icons.Filled.Home
}
