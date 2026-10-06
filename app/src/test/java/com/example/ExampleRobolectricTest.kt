package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.IslandPreferences
import com.example.data.IslandStateManager
import com.example.model.IslandEventType
import com.example.model.NotchPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dynamic Island", appName)
  }

  @Test
  fun `test island state manager initialization and events`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val stateManager = IslandStateManager.getInstance(context)

    assertNotNull(stateManager.config.value)
    stateManager.dismissAll()
    stateManager.triggerChargingMock(true, 90)

    val currentEvent = stateManager.primaryEvent.value
    assertNotNull(currentEvent)
    assertEquals(IslandEventType.CHARGING, currentEvent?.type)
    assertEquals(90, currentEvent?.batteryPercent)
  }

  @Test
  fun `test island preferences load and save`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = IslandPreferences(context)
    val initialConfig = prefs.loadConfig()

    val updatedConfig = initialConfig.copy(
        pillWidth = 140,
        xOffset = 15,
        notchPosition = NotchPosition.LEFT
    )
    prefs.saveConfig(updatedConfig)

    val reloadedConfig = prefs.loadConfig()
    assertEquals(140, reloadedConfig.pillWidth)
    assertEquals(15, reloadedConfig.xOffset)
    assertEquals(NotchPosition.LEFT, reloadedConfig.notchPosition)
  }
}
