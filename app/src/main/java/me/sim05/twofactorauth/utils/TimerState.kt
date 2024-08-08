package me.sim05.twofactorauth.utils

data class TimerState(
    val timeInMillis: Long = 0L,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
    val isDone: Boolean = true,
)