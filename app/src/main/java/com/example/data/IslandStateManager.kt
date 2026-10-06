package com.example.data

import android.content.Context
import com.example.model.IslandConfig
import com.example.model.IslandEvent
import com.example.model.IslandEventType
import com.example.model.IslandExpansionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IslandStateManager private constructor(context: Context) {

    private val prefs = IslandPreferences(context)
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _config = MutableStateFlow(prefs.loadConfig())
    val config: StateFlow<IslandConfig> = _config.asStateFlow()

    private val _primaryEvent = MutableStateFlow<IslandEvent?>(null)
    val primaryEvent: StateFlow<IslandEvent?> = _primaryEvent.asStateFlow()

    private val _secondaryEvent = MutableStateFlow<IslandEvent?>(null)
    val secondaryEvent: StateFlow<IslandEvent?> = _secondaryEvent.asStateFlow()

    private val _expansionState = MutableStateFlow(IslandExpansionState.COLLAPSED)
    val expansionState: StateFlow<IslandExpansionState> = _expansionState.asStateFlow()

    private var autoDismissJob: Job? = null
    private var timerJob: Job? = null
    private var musicProgressJob: Job? = null

    init {
        // Initial state: Start with a cool welcome demo or collapsed state
        setupInitialWelcome()
    }

    private fun setupInitialWelcome() {
        val initialMusic = IslandEvent(
            id = "initial_music",
            type = IslandEventType.MUSIC,
            title = "Starboy",
            subtitle = "The Weeknd ft. Daft Punk",
            extraInfo = "Spotify",
            progress = 0.35f,
            isPlaying = true,
            durationText = "1:24",
            remainingText = "-2:26",
            appName = "Spotify"
        )
        _primaryEvent.value = initialMusic
        _expansionState.value = IslandExpansionState.COMPACT
        startMusicProgressTicker()
    }

    fun updateConfig(newConfig: IslandConfig) {
        _config.value = newConfig
        prefs.saveConfig(newConfig)
    }

    fun setExpansion(state: IslandExpansionState) {
        _expansionState.value = state
        if (state == IslandExpansionState.COLLAPSED && _primaryEvent.value?.type == IslandEventType.NOTIFICATION) {
            // Dismiss temporary notifications when collapsed
            _primaryEvent.value = null
        }
    }

    fun toggleExpansion() {
        _expansionState.value = when (_expansionState.value) {
            IslandExpansionState.COLLAPSED -> IslandExpansionState.COMPACT
            IslandExpansionState.COMPACT -> IslandExpansionState.EXPANDED
            IslandExpansionState.EXPANDED -> IslandExpansionState.COMPACT
        }
    }

    fun postEvent(event: IslandEvent, autoExpand: Boolean = true) {
        val currentPrimary = _primaryEvent.value
        val cfg = _config.value

        // Check if event type is enabled
        when (event.type) {
            IslandEventType.MUSIC -> if (!cfg.musicEnabled) return
            IslandEventType.CHARGING -> if (!cfg.chargingEnabled) return
            IslandEventType.NOTIFICATION -> if (!cfg.notificationsEnabled) return
            IslandEventType.TIMER -> if (!cfg.timerEnabled) return
            IslandEventType.BLUETOOTH -> if (!cfg.bluetoothEnabled) return
            IslandEventType.CALL -> if (!cfg.callEnabled) return
            else -> {}
        }

        if (currentPrimary != null && cfg.dualPillEnabled && currentPrimary.id != event.id) {
            // Secondary pill logic
            if (currentPrimary.type == IslandEventType.MUSIC && event.type != IslandEventType.MUSIC) {
                // Secondary takes the new short event or timer
                _secondaryEvent.value = event
                _expansionState.value = IslandExpansionState.COMPACT
            } else {
                _secondaryEvent.value = currentPrimary
                _primaryEvent.value = event
                _expansionState.value = if (autoExpand) IslandExpansionState.COMPACT else IslandExpansionState.COLLAPSED
            }
        } else {
            _primaryEvent.value = event
            _expansionState.value = if (autoExpand) IslandExpansionState.COMPACT else IslandExpansionState.COLLAPSED
        }

        // Auto dismiss for temporary notifications or charging
        scheduleAutoDismiss(event)
    }

    private fun scheduleAutoDismiss(event: IslandEvent) {
        autoDismissJob?.cancel()
        if (event.type == IslandEventType.NOTIFICATION ||
            event.type == IslandEventType.CHARGING ||
            event.type == IslandEventType.BATTERY_LOW ||
            event.type == IslandEventType.BLUETOOTH ||
            event.type == IslandEventType.RINGER
        ) {
            val delaySeconds = _config.value.autoHideDelaySeconds
            if (delaySeconds > 0) {
                autoDismissJob = scope.launch {
                    delay(delaySeconds * 1000L)
                    if (_expansionState.value != IslandExpansionState.EXPANDED) {
                        if (_primaryEvent.value?.id == event.id) {
                            if (_secondaryEvent.value != null) {
                                _primaryEvent.value = _secondaryEvent.value
                                _secondaryEvent.value = null
                            } else {
                                _primaryEvent.value = null
                                _expansionState.value = IslandExpansionState.COLLAPSED
                            }
                        } else if (_secondaryEvent.value?.id == event.id) {
                            _secondaryEvent.value = null
                        }
                    }
                }
            }
        }
    }

    fun dismissEvent(id: String) {
        if (_primaryEvent.value?.id == id) {
            _primaryEvent.value = _secondaryEvent.value
            _secondaryEvent.value = null
            if (_primaryEvent.value == null) {
                _expansionState.value = IslandExpansionState.COLLAPSED
            }
        } else if (_secondaryEvent.value?.id == id) {
            _secondaryEvent.value = null
        }
    }

    fun dismissAll() {
        autoDismissJob?.cancel()
        timerJob?.cancel()
        musicProgressJob?.cancel()
        _primaryEvent.value = null
        _secondaryEvent.value = null
        _expansionState.value = IslandExpansionState.COLLAPSED
    }

    // Music Player Simulation / Controls
    fun togglePlayPause() {
        val current = _primaryEvent.value ?: return
        if (current.type == IslandEventType.MUSIC) {
            _primaryEvent.value = current.copy(isPlaying = !current.isPlaying)
        }
    }

    fun nextTrack() {
        val songs = listOf(
            Triple("Blinding Lights", "The Weeknd", "Spotify"),
            Triple("As It Was", "Harry Styles", "Apple Music"),
            Triple("Shape of You", "Ed Sheeran", "YouTube Music"),
            Triple("Stay", "The Kid LAROI & Justin Bieber", "Spotify")
        )
        val nextSong = songs.random()
        postEvent(
            IslandEvent(
                id = "music_track",
                type = IslandEventType.MUSIC,
                title = nextSong.first,
                subtitle = nextSong.second,
                extraInfo = nextSong.third,
                progress = 0.05f,
                isPlaying = true,
                durationText = "0:12",
                remainingText = "-3:10",
                appName = nextSong.third
            )
        )
        startMusicProgressTicker()
    }

    fun prevTrack() {
        nextTrack()
    }

    private fun startMusicProgressTicker() {
        musicProgressJob?.cancel()
        musicProgressJob = scope.launch {
            while (true) {
                delay(1000)
                val cur = _primaryEvent.value
                if (cur != null && cur.type == IslandEventType.MUSIC && cur.isPlaying) {
                    val newProg = (cur.progress + 0.015f).coerceAtMost(1f)
                    val elapsedSec = (newProg * 210).toInt()
                    val totalSec = 210
                    val remainSec = totalSec - elapsedSec
                    val durStr = String.format("%d:%02d", elapsedSec / 60, elapsedSec % 60)
                    val remStr = String.format("-%d:%02d", remainSec / 60, remainSec % 60)
                    _primaryEvent.value = cur.copy(
                        progress = newProg,
                        durationText = durStr,
                        remainingText = remStr
                    )
                }
            }
        }
    }

    // Trigger Mocks for Instant Testing / Simulator
    fun triggerMusicMock() {
        val event = IslandEvent(
            id = "mock_music",
            type = IslandEventType.MUSIC,
            title = "Flowers",
            subtitle = "Miley Cyrus",
            extraInfo = "Spotify",
            progress = 0.42f,
            isPlaying = true,
            durationText = "1:32",
            remainingText = "-1:48",
            appName = "Spotify"
        )
        postEvent(event, autoExpand = true)
        startMusicProgressTicker()
    }

    fun triggerChargingMock(isCharging: Boolean = true, percent: Int = 84) {
        val event = IslandEvent(
            id = "mock_charging",
            type = IslandEventType.CHARGING,
            title = if (isCharging) "جاري الشحن السريع" else "تم فصل الشاحن",
            subtitle = "$percent% كفاءة البطارية",
            batteryPercent = percent,
            isCharging = isCharging
        )
        postEvent(event, autoExpand = true)
    }

    fun triggerBatteryLowMock(percent: Int = 15) {
        val event = IslandEvent(
            id = "mock_battery_low",
            type = IslandEventType.BATTERY_LOW,
            title = "البطارية منخفضة",
            subtitle = "$percent% متبقي - يرجى توصيل الشاحن",
            batteryPercent = percent
        )
        postEvent(event, autoExpand = true)
    }

    fun triggerNotificationMock(
        sender: String = "أحمد محمد",
        message: String = "يا صديقي، جاهز نتقابل النهاردة الساعة ٧؟",
        appName: String = "واتساب"
    ) {
        val event = IslandEvent(
            id = "mock_notif_${System.currentTimeMillis()}",
            type = IslandEventType.NOTIFICATION,
            title = sender,
            subtitle = message,
            appName = appName
        )
        postEvent(event, autoExpand = true)
    }

    fun triggerCallMock(caller: String = "سارة حسن") {
        val event = IslandEvent(
            id = "mock_call",
            type = IslandEventType.CALL,
            title = caller,
            subtitle = "مكالمة هاتفية جارية • 00:45",
            extraInfo = "هاتف محمول"
        )
        postEvent(event, autoExpand = true)
    }

    fun triggerTimerMock(seconds: Int = 30) {
        timerJob?.cancel()
        val eventId = "mock_timer"
        var remaining = seconds
        val event = IslandEvent(
            id = eventId,
            type = IslandEventType.TIMER,
            title = "مؤقت",
            subtitle = String.format("%02d:%02d", remaining / 60, remaining % 60),
            extraInfo = "عداد تنازلي",
            progress = 1.0f
        )
        postEvent(event, autoExpand = true)

        timerJob = scope.launch {
            while (remaining > 0) {
                delay(1000)
                remaining--
                val curSec = String.format("%02d:%02d", remaining / 60, remaining % 60)
                val prog = remaining.toFloat() / seconds.toFloat()
                if (_primaryEvent.value?.id == eventId) {
                    _primaryEvent.value = _primaryEvent.value?.copy(subtitle = curSec, progress = prog)
                } else if (_secondaryEvent.value?.id == eventId) {
                    _secondaryEvent.value = _secondaryEvent.value?.copy(subtitle = curSec, progress = prog)
                }
            }
            // Finished
            delay(1500)
            dismissEvent(eventId)
        }
    }

    fun triggerBluetoothMock(device: String = "AirPods Pro") {
        val event = IslandEvent(
            id = "mock_bt",
            type = IslandEventType.BLUETOOTH,
            title = device,
            subtitle = "تم الاتصال بنجاح • 98%",
            extraInfo = "Bluetooth 5.3"
        )
        postEvent(event, autoExpand = true)
    }

    fun triggerRingerMock(mode: String = "صامت") {
        val event = IslandEvent(
            id = "mock_ringer",
            type = IslandEventType.RINGER,
            title = "وضع الرنين",
            subtitle = mode,
            isMuted = mode == "صامت"
        )
        postEvent(event, autoExpand = true)
    }

    companion object {
        @Volatile
        private var instance: IslandStateManager? = null

        fun getInstance(context: Context): IslandStateManager {
            return instance ?: synchronized(this) {
                instance ?: IslandStateManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
