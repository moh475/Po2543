package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.DynamicIslandApp
import com.example.MainActivity
import com.example.R
import com.example.data.IslandStateManager
import com.example.model.IslandExpansionState
import com.example.model.NotchPosition
import com.example.ui.island.DynamicIslandView
import com.example.ui.theme.DynamicIslandTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DynamicIslandOverlayService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val appViewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry = savedStateRegistryController.savedStateRegistry
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = appViewModelStore

    private var windowManager: WindowManager? = null
    private var composeView: ComposeView? = null
    private lateinit var stateManager: IslandStateManager
    private lateinit var batteryMonitor: BatteryMonitor
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var currentLayoutParams: WindowManager.LayoutParams? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        stateManager = IslandStateManager.getInstance(this)
        batteryMonitor = BatteryMonitor(this)
        batteryMonitor.start()

        startAsForeground()

        if (Settings.canDrawOverlays(this)) {
            initOverlayView()
            observeState()
        }
    }

    private fun startAsForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, DynamicIslandApp.OVERLAY_CHANNEL_ID)
            .setContentTitle(getString(R.string.service_running_title))
            .setContentText(getString(R.string.service_running_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(DynamicIslandApp.OVERLAY_NOTIFICATION_ID, notification)
    }

    private fun initOverlayView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setViewTreeLifecycleOwner(this@DynamicIslandOverlayService)
            setViewTreeViewModelStoreOwner(this@DynamicIslandOverlayService)
            setViewTreeSavedStateRegistryOwner(this@DynamicIslandOverlayService)

            setContent {
                DynamicIslandTheme(darkTheme = true) {
                    val config by stateManager.config.collectAsState()
                    val primaryEvent by stateManager.primaryEvent.collectAsState()
                    val secondaryEvent by stateManager.secondaryEvent.collectAsState()
                    val expansionState by stateManager.expansionState.collectAsState()

                    if (config.isEnabled) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            val xOffsetDp = when (config.notchPosition) {
                                NotchPosition.LEFT -> (-90 + config.xOffset).dp
                                NotchPosition.RIGHT -> (90 + config.xOffset).dp
                                NotchPosition.CENTER -> config.xOffset.dp
                                NotchPosition.CUSTOM -> config.xOffset.dp
                            }

                            DynamicIslandView(
                                config = config,
                                primaryEvent = primaryEvent,
                                secondaryEvent = secondaryEvent,
                                expansionState = expansionState,
                                onToggleExpansion = { stateManager.toggleExpansion() },
                                onSetExpansion = { stateManager.setExpansion(it) },
                                onPlayPause = { stateManager.togglePlayPause() },
                                onNextTrack = { stateManager.nextTrack() },
                                onPrevTrack = { stateManager.prevTrack() },
                                onDismissPrimary = { primaryEvent?.let { stateManager.dismissEvent(it.id) } },
                                modifier = Modifier.offset(x = xOffsetDp, y = config.yOffset.dp)
                            )
                        }
                    }
                }
            }
        }

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            dpToPx(65),
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            x = 0
            y = 0
        }

        currentLayoutParams = params

        try {
            windowManager?.addView(composeView, params)
        } catch (_: Exception) {}
    }

    private fun observeState() {
        serviceScope.launch {
            stateManager.expansionState.collectLatest { expansion ->
                updateWindowSize(expansion)
            }
        }
    }

    private fun updateWindowSize(expansion: IslandExpansionState) {
        val params = currentLayoutParams ?: return
        val wm = windowManager ?: return
        val cv = composeView ?: return

        val targetHeight = when (expansion) {
            IslandExpansionState.COLLAPSED -> dpToPx(55)
            IslandExpansionState.COMPACT -> dpToPx(70)
            IslandExpansionState.EXPANDED -> dpToPx(240)
        }

        if (params.height != targetHeight) {
            params.height = targetHeight
            try {
                wm.updateViewLayout(cv, params)
            } catch (_: Exception) {}
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        batteryMonitor.stop()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        appViewModelStore.clear()

        if (composeView != null && windowManager != null) {
            try {
                windowManager?.removeView(composeView)
            } catch (_: Exception) {}
            composeView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
