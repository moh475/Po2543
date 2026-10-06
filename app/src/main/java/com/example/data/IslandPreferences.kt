package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.IslandConfig
import com.example.model.NotchPosition

class IslandPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun loadConfig(): IslandConfig {
        val positionStr = prefs.getString(KEY_NOTCH_POSITION, NotchPosition.CENTER.name) ?: NotchPosition.CENTER.name
        val notchPosition = try {
            NotchPosition.valueOf(positionStr)
        } catch (_: Exception) {
            NotchPosition.CENTER
        }

        return IslandConfig(
            isEnabled = prefs.getBoolean(KEY_IS_ENABLED, true),
            notchPosition = notchPosition,
            pillWidth = prefs.getInt(KEY_PILL_WIDTH, 125),
            pillHeight = prefs.getInt(KEY_PILL_HEIGHT, 35),
            yOffset = prefs.getInt(KEY_Y_OFFSET, 10),
            xOffset = prefs.getInt(KEY_X_OFFSET, 0),
            cornerRadius = prefs.getInt(KEY_CORNER_RADIUS, 20),
            isGlowEnabled = prefs.getBoolean(KEY_IS_GLOW_ENABLED, true),
            hapticsEnabled = prefs.getBoolean(KEY_HAPTICS_ENABLED, true),
            dualPillEnabled = prefs.getBoolean(KEY_DUAL_PILL_ENABLED, true),
            autoHideDelaySeconds = prefs.getInt(KEY_AUTO_HIDE_DELAY, 5),

            musicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true),
            musicColor = prefs.getLong(KEY_MUSIC_COLOR, 0xFFBF5AF2L),
            musicIconIndex = prefs.getInt(KEY_MUSIC_ICON_INDEX, 0),

            callEnabled = prefs.getBoolean(KEY_CALL_ENABLED, true),
            callColor = prefs.getLong(KEY_CALL_COLOR, 0xFF30D158L),
            callIconIndex = prefs.getInt(KEY_CALL_ICON_INDEX, 0),

            timerEnabled = prefs.getBoolean(KEY_TIMER_ENABLED, true),
            timerColor = prefs.getLong(KEY_TIMER_COLOR, 0xFFFF9F0AL),
            timerIconIndex = prefs.getInt(KEY_TIMER_ICON_INDEX, 0),

            chargingEnabled = prefs.getBoolean(KEY_CHARGING_ENABLED, true),
            chargingColor = prefs.getLong(KEY_CHARGING_COLOR, 0xFF30D158L),
            chargingIconIndex = prefs.getInt(KEY_CHARGING_ICON_INDEX, 0),

            batteryLowEnabled = prefs.getBoolean(KEY_BATTERY_LOW_ENABLED, true),
            batteryLowColor = prefs.getLong(KEY_BATTERY_LOW_COLOR, 0xFFFF453AL),
            batteryLowIconIndex = prefs.getInt(KEY_BATTERY_LOW_ICON_INDEX, 0),

            notificationsEnabled = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true),
            notificationsColor = prefs.getLong(KEY_NOTIFICATIONS_COLOR, 0xFF0A84FFL),
            notificationsIconIndex = prefs.getInt(KEY_NOTIFICATIONS_ICON_INDEX, 0),

            bluetoothEnabled = prefs.getBoolean(KEY_BLUETOOTH_ENABLED, true),
            bluetoothColor = prefs.getLong(KEY_BLUETOOTH_COLOR, 0xFF64D2FFL),
            bluetoothIconIndex = prefs.getInt(KEY_BLUETOOTH_ICON_INDEX, 0)
        )
    }

    fun saveConfig(config: IslandConfig) {
        prefs.edit().apply {
            putBoolean(KEY_IS_ENABLED, config.isEnabled)
            putString(KEY_NOTCH_POSITION, config.notchPosition.name)
            putInt(KEY_PILL_WIDTH, config.pillWidth)
            putInt(KEY_PILL_HEIGHT, config.pillHeight)
            putInt(KEY_Y_OFFSET, config.yOffset)
            putInt(KEY_X_OFFSET, config.xOffset)
            putInt(KEY_CORNER_RADIUS, config.cornerRadius)
            putBoolean(KEY_IS_GLOW_ENABLED, config.isGlowEnabled)
            putBoolean(KEY_HAPTICS_ENABLED, config.hapticsEnabled)
            putBoolean(KEY_DUAL_PILL_ENABLED, config.dualPillEnabled)
            putInt(KEY_AUTO_HIDE_DELAY, config.autoHideDelaySeconds)

            putBoolean(KEY_MUSIC_ENABLED, config.musicEnabled)
            putLong(KEY_MUSIC_COLOR, config.musicColor)
            putInt(KEY_MUSIC_ICON_INDEX, config.musicIconIndex)

            putBoolean(KEY_CALL_ENABLED, config.callEnabled)
            putLong(KEY_CALL_COLOR, config.callColor)
            putInt(KEY_CALL_ICON_INDEX, config.callIconIndex)

            putBoolean(KEY_TIMER_ENABLED, config.timerEnabled)
            putLong(KEY_TIMER_COLOR, config.timerColor)
            putInt(KEY_TIMER_ICON_INDEX, config.timerIconIndex)

            putBoolean(KEY_CHARGING_ENABLED, config.chargingEnabled)
            putLong(KEY_CHARGING_COLOR, config.chargingColor)
            putInt(KEY_CHARGING_ICON_INDEX, config.chargingIconIndex)

            putBoolean(KEY_BATTERY_LOW_ENABLED, config.batteryLowEnabled)
            putLong(KEY_BATTERY_LOW_COLOR, config.batteryLowColor)
            putInt(KEY_BATTERY_LOW_ICON_INDEX, config.batteryLowIconIndex)

            putBoolean(KEY_NOTIFICATIONS_ENABLED, config.notificationsEnabled)
            putLong(KEY_NOTIFICATIONS_COLOR, config.notificationsColor)
            putInt(KEY_NOTIFICATIONS_ICON_INDEX, config.notificationsIconIndex)

            putBoolean(KEY_BLUETOOTH_ENABLED, config.bluetoothEnabled)
            putLong(KEY_BLUETOOTH_COLOR, config.bluetoothColor)
            putInt(KEY_BLUETOOTH_ICON_INDEX, config.bluetoothIconIndex)

            apply()
        }
    }

    companion object {
        private const val PREF_NAME = "dynamic_island_prefs"
        private const val KEY_IS_ENABLED = "is_enabled"
        private const val KEY_NOTCH_POSITION = "notch_position"
        private const val KEY_PILL_WIDTH = "pill_width"
        private const val KEY_PILL_HEIGHT = "pill_height"
        private const val KEY_Y_OFFSET = "y_offset"
        private const val KEY_X_OFFSET = "x_offset"
        private const val KEY_CORNER_RADIUS = "corner_radius"
        private const val KEY_IS_GLOW_ENABLED = "is_glow_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_DUAL_PILL_ENABLED = "dual_pill_enabled"
        private const val KEY_AUTO_HIDE_DELAY = "auto_hide_delay"

        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_MUSIC_COLOR = "music_color"
        private const val KEY_MUSIC_ICON_INDEX = "music_icon_index"

        private const val KEY_CALL_ENABLED = "call_enabled"
        private const val KEY_CALL_COLOR = "call_color"
        private const val KEY_CALL_ICON_INDEX = "call_icon_index"

        private const val KEY_TIMER_ENABLED = "timer_enabled"
        private const val KEY_TIMER_COLOR = "timer_color"
        private const val KEY_TIMER_ICON_INDEX = "timer_icon_index"

        private const val KEY_CHARGING_ENABLED = "charging_enabled"
        private const val KEY_CHARGING_COLOR = "charging_color"
        private const val KEY_CHARGING_ICON_INDEX = "charging_icon_index"

        private const val KEY_BATTERY_LOW_ENABLED = "battery_low_enabled"
        private const val KEY_BATTERY_LOW_COLOR = "battery_low_color"
        private const val KEY_BATTERY_LOW_ICON_INDEX = "battery_low_icon_index"

        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_NOTIFICATIONS_COLOR = "notifications_color"
        private const val KEY_NOTIFICATIONS_ICON_INDEX = "notifications_icon_index"

        private const val KEY_BLUETOOTH_ENABLED = "bluetooth_enabled"
        private const val KEY_BLUETOOTH_COLOR = "bluetooth_color"
        private const val KEY_BLUETOOTH_ICON_INDEX = "bluetooth_icon_index"
    }
}
