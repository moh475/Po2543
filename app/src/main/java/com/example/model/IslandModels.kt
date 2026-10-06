package com.example.model

enum class IslandExpansionState {
    COLLAPSED,   // Minimal pill matching camera hole
    COMPACT,     // Informative pill with icon & status
    EXPANDED     // Fully opened interactive Apple-style card
}

enum class IslandEventType {
    NONE,
    MUSIC,
    CHARGING,
    BATTERY_LOW,
    NOTIFICATION,
    CALL,
    TIMER,
    BLUETOOTH,
    RINGER
}

data class IslandEvent(
    val id: String = System.currentTimeMillis().toString(),
    val type: IslandEventType,
    val title: String,
    val subtitle: String = "",
    val extraInfo: String = "",
    val progress: Float = 0f,
    val isPlaying: Boolean = true,
    val durationText: String = "",
    val remainingText: String = "",
    val appName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val isMuted: Boolean = false
)

enum class NotchPosition {
    CENTER,
    LEFT,
    RIGHT,
    CUSTOM
}

data class IslandConfig(
    val isEnabled: Boolean = true,
    val notchPosition: NotchPosition = NotchPosition.CENTER,
    val pillWidth: Int = 125,
    val pillHeight: Int = 35,
    val yOffset: Int = 10,
    val xOffset: Int = 0,
    val cornerRadius: Int = 20,
    val isGlowEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val dualPillEnabled: Boolean = true,
    val autoHideDelaySeconds: Int = 5,

    // Notification categories configuration (Enabled + Custom Color + Custom Icon)
    val musicEnabled: Boolean = true,
    val musicColor: Long = 0xFFBF5AF2L,
    val musicIconIndex: Int = 0,

    val callEnabled: Boolean = true,
    val callColor: Long = 0xFF30D158L,
    val callIconIndex: Int = 0,

    val timerEnabled: Boolean = true,
    val timerColor: Long = 0xFFFF9F0AL,
    val timerIconIndex: Int = 0,

    val chargingEnabled: Boolean = true,
    val chargingColor: Long = 0xFF30D158L,
    val chargingIconIndex: Int = 0,

    val batteryLowEnabled: Boolean = true,
    val batteryLowColor: Long = 0xFFFF453AL,
    val batteryLowIconIndex: Int = 0,

    val notificationsEnabled: Boolean = true,
    val notificationsColor: Long = 0xFF0A84FFL,
    val notificationsIconIndex: Int = 0,

    val bluetoothEnabled: Boolean = true,
    val bluetoothColor: Long = 0xFF64D2FFL,
    val bluetoothIconIndex: Int = 0
)
