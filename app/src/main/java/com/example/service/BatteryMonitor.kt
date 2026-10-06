package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.data.IslandStateManager

class BatteryMonitor(private val context: Context) {
    private val stateManager = IslandStateManager.getInstance(context)
    private var isRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            val action = intent.action ?: return

            when (action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    val batteryPct = getBatteryPercentage(intent)
                    stateManager.triggerChargingMock(isCharging = true, percent = batteryPct)
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    val batteryPct = getBatteryPercentage(intent)
                    stateManager.triggerChargingMock(isCharging = false, percent = batteryPct)
                }
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                            status == BatteryManager.BATTERY_STATUS_FULL

                    if (level != -1 && scale > 0) {
                        val pct = ((level / scale.toFloat()) * 100).toInt()
                        if (pct <= 15 && !isCharging) {
                            stateManager.triggerBatteryLowMock(percent = pct)
                        }
                    }
                }
            }
        }
    }

    fun start() {
        if (!isRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
                addAction(Intent.ACTION_BATTERY_CHANGED)
            }
            context.registerReceiver(receiver, filter)
            isRegistered = true
        }
    }

    fun stop() {
        if (isRegistered) {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
            isRegistered = false
        }
    }

    private fun getBatteryPercentage(intent: Intent): Int {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level >= 0 && scale > 0) {
            return ((level / scale.toFloat()) * 100).toInt()
        }
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        return bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 80
    }
}
