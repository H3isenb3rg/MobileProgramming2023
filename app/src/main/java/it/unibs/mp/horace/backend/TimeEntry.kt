package it.unibs.mp.horace.backend

import java.time.Instant

data class TimeEntry(
    val idActivity: String,
    val description: String,
    val isPomodoro: Boolean,
    val startTime: String,
    val endTime: String
    ) {
    var uid: String? = null
    // FIXME: Currently date times are strings we need to find the correct type to use
}