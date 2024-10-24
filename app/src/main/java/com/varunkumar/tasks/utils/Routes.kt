package com.varunkumar.tasks.utils

import kotlinx.serialization.Serializable

interface ScreenRoute {
    val route: String
    val title: String
}

@Serializable
object HomeScreen : ScreenRoute {
    override val route: String = "home_screen"
    override val title: String = "Tasks"
}

@Serializable
object CalendarScreen : ScreenRoute {
    override val route: String = "calendar_screen"
    override val title: String = "Calendar"
}