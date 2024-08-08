package me.sim05.twofactorauth.utils

import android.util.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds

class RepeatingTimer(private val interval: Long) {
    private var timer: Job? = null
    private var goalTime: Long = 0
    private var isRunning = false

    private val timeInMillisFlow = MutableStateFlow(0L)
    private val progressFlow = MutableStateFlow(0f)
    private val isPlayingFlow = MutableStateFlow(false)
    private val isDoneFlow = MutableStateFlow(true)

    init {
        goalTime = (System.currentTimeMillis().floorDiv(30) * 30) + 30.seconds.inWholeMilliseconds
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
            while (goalTime - System.currentTimeMillis() > 0) {
                Log.v("timerCheck", "timer is running: ${timeInMillisFlow.value}, ${goalTime - System.currentTimeMillis()}")
                delay(1000)
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
        goalTime = (System.currentTimeMillis().floorDiv(30) * 30) + 30.seconds.inWholeMilliseconds
        isRunning = false
        start()
    }

    private fun onTimerTick() {
        timeInMillisFlow.value = ceil((goalTime - System.currentTimeMillis()).div(1000f)).toLong()
        Log.v("timerCheck", "timer: ${timeInMillisFlow.value}")
        progressFlow.value = (timeInMillisFlow.value.toFloat() / interval.toFloat()) * 100
        isPlayingFlow.value = isRunning
        isDoneFlow.value = timeInMillisFlow.value == 0L
    }
}