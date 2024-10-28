package com.varunkumar.tasks.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.varunkumar.tasks.models.Task
import kotlinx.serialization.Serializable

interface ScreenRoute {
    val route: String
    val title: String
    val outlinedIcon: ImageVector
    val filledIcon: ImageVector
}

@Serializable
object HomeScreen : ScreenRoute {
    override val route: String = "home_screen"
    override val title: String = "Tasks"
    override val outlinedIcon: ImageVector = Icons.Outlined.Home
    override val filledIcon: ImageVector = Icons.Filled.Home
}

@Serializable
object SignInScreen : ScreenRoute {
    override val route: String = "sign_in_screen"
    override val title: String = "Profile"
    override val outlinedIcon: ImageVector = Icons.Outlined.Home
    override val filledIcon: ImageVector = Icons.Filled.Home
}
