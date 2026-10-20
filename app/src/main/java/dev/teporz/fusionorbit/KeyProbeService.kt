package dev.teporz.fusionorbit

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class KeyProbeService : AccessibilityService() {
 private var volumeDownHeld = false
 private var currentPackage = "unknown"

 override fun onServiceConnected() {
  super.onServiceConnected()
  val info = serviceInfo
  info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
  serviceInfo = info
  EventLog.recordStatus("ACCESSIBILITY connected; filterRequested=" +
   ((serviceInfo.flags and AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS) != 0))
 }
 override fun onAccessibilityEvent(event: AccessibilityEvent?) {
  if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
   val next = event.packageName?.toString() ?: "unknown"
   if (next != currentPackage) {
    currentPackage = next
    EventLog.recordStatus("Foreground package: $currentPackage")
   }
  }
 }
 override fun onInterrupt() {
  release("service interrupted")
 }
 override fun onDestroy() {
  release("service destroyed")
  super.onDestroy()
 }
 private fun release(reason: String) {
  if (volumeDownHeld) {
   volumeDownHeld = false
   EventLog.recordStatus("ORBIT released ($reason)")
  }
 }
 override fun onKeyEvent(event: KeyEvent): Boolean {
  EventLog.record("ACCESSIBILITY", event)
  if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
   when(event.action) {
    KeyEvent.ACTION_DOWN -> if (!volumeDownHeld) {
     volumeDownHeld = true
     EventLog.recordStatus("ORBIT held=true (diagnostic only; Shift NOT injected)")
    }
    KeyEvent.ACTION_UP -> release("volume down released")
   }
  }
  // Do not swallow volume keys until a working Shift transport exists.
  return false
 }
}