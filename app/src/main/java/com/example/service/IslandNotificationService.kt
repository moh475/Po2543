package com.example.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.data.IslandStateManager
import com.example.model.IslandEvent
import com.example.model.IslandEventType

class IslandNotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val packageName = sbn.packageName ?: return
        if (packageName == applicationContext.packageName) return // Don't intercept own notifications

        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        if (title.isEmpty() && text.isEmpty()) return

        val appName = try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            packageName
        }

        val stateManager = IslandStateManager.getInstance(applicationContext)

        // Check if media session notification
        val isMedia = sbn.notification.category == Notification.CATEGORY_TRANSPORT ||
                extras.containsKey(Notification.EXTRA_MEDIA_SESSION)

        if (isMedia) {
            stateManager.postEvent(
                IslandEvent(
                    id = "media_${sbn.id}",
                    type = IslandEventType.MUSIC,
                    title = title.ifEmpty { "تشغيل وسائط" },
                    subtitle = text,
                    appName = appName,
                    isPlaying = true
                )
            )
        } else {
            // General notification
            stateManager.postEvent(
                IslandEvent(
                    id = "notif_${sbn.id}",
                    type = IslandEventType.NOTIFICATION,
                    title = title.ifEmpty { appName },
                    subtitle = text,
                    appName = appName
                )
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn ?: return
        val stateManager = IslandStateManager.getInstance(applicationContext)
        stateManager.dismissEvent("notif_${sbn.id}")
        stateManager.dismissEvent("media_${sbn.id}")
    }
}
