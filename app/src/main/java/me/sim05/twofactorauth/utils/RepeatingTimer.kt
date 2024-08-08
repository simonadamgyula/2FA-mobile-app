package me.sim05.twofactorauth.utils

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class RepeatingTimer(private val interval: Long) {
    private var timer: Job? = null
    private var remainingTime: Long = 0
    private var isRunning = false

    private val timeInMillisFlow = MutableStateFlow(0L)
    private val progressFlow = MutableStateFlow(0f)
    private val isPlayingFlow = MutableStateFlow(false)
    private val isDoneFlow = MutableStateFlow(true)

    init {
        remainingTime = interval
    }

    val timerState = combine(
        timeInMillisFlow,
        progressFlow,
        isPlayingFlow,
        isDoneFlow
    ) { timeInMillis, progress, isPlaying, isDone ->
        TimerState(timeInMillis, progress, isPlaying, isDone)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        if (isRunning) return

        timer = GlobalScope.launch {
            while (remainingTime > 0) {
                delay(1.seconds.inWholeMilliseconds)
                Log.v("timer", "remainingTime: $remainingTime")
                remainingTime -= 1.seconds.inWholeMilliseconds
                onTimerTick()
            }
            restart()
        }
        isRunning = true
    }

    fun pause() {
        timer?.cancel()
        isRunning = false
    }

    fun restart() {
        timer?.cancel()
        remainingTime = interval
        isRunning = false
        start()
    }

    private fun onTimerTick() {
        Log.v("timer", "remainingTime: $remainingTime")

        timeInMillisFlow.value = remainingTime
        progressFlow.value = (remainingTime.toFloat() / interval.toFloat()) * 100
        isPlayingFlow.value = isRunning
        isDoneFlow.value = remainingTime == 0L
    }
}