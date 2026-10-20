package dev.teporz.fusionorbit

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

/** Gboard-compatible diagnostic. Does NOT inject Shift or consume volume keys. */
class KeyProbeService : AccessibilityService() {
 private var held = false
 private var foreground = "unknown"
 override fun onServiceConnected() {
  super.onServiceConnected()
  val info = serviceInfo
  info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
  serviceInfo = info
  EventLog.recordStatus("Accessibility connected; Gboard preserved; no Shift injection")
 }
 override fun onAccessibilityEvent(event: AccessibilityEvent?) {
  if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
  val next = event.packageName?.toString() ?: "unknown"
  if (next == foreground) return
  if (held) release("foreground changed")
  foreground = next
  EventLog.recordStatus("Foreground package: $foreground")
 }
 private fun release(reason: String) {
  if (!held) return
  held = false
  EventLog.recordStatus("VOLUME_DOWN released ($reason); no Shift sent")
 }
 override fun onInterrupt() { release("interrupted") }
 override fun onDestroy() { release("service destroyed"); super.onDestroy() }
 override fun onKeyEvent(event: KeyEvent): Boolean {
  if (event.keyCode != KeyEvent.KEYCODE_VOLUME_DOWN) return false
  EventLog.record("ACCESSIBILITY", event)
  if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
   if (!held) {
    held = true
    EventLog.recordStatus("VOLUME_DOWN held; target=${foreground == "com.microsoft.rdc.androidx"}; Shift transport pending")
   }
  } else if (event.action == KeyEvent.ACTION_UP) release("physical key up")
  return false
 }
}