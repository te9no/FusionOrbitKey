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
  EventLog.recordStatus("ACCESSIBILITY connected")
 }
 override fun onAccessibilityEvent(event: AccessibilityEvent?) {
  if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
   val next = event.packageName?.toString() ?: "unknown"
   if (next != currentPackage) {
    if (currentPackage == "com.microsoft.rdc.androidx" && next != currentPackage) release("window changed")
    currentPackage = next
    EventLog.recordStatus("Foreground package: $currentPackage")
   }
  }
 }
 override fun onInterrupt() { release("interrupted") }
 override fun onDestroy() { release("destroyed"); super.onDestroy() }
 private fun release(reason: String) {
  if (volumeDownHeld) {
   volumeDownHeld = false
   OrbitIme.release()
   EventLog.recordStatus("ORBIT released ($reason)")
  }
 }
 override fun onKeyEvent(event: KeyEvent): Boolean {
  if (event.keyCode != KeyEvent.KEYCODE_VOLUME_DOWN) return false
  EventLog.record("ACCESSIBILITY", event)
  // This is an experimental IME transport, not system-wide key injection.
  // Do not consume the physical key unless InputConnection accepted the event.
  if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0 &&
      currentPackage == "com.microsoft.rdc.androidx") {
   val sent = OrbitIme.shift(true)
   volumeDownHeld = sent
   EventLog.recordStatus("ORBIT press accepted=$sent")
   return sent
  }
  if (event.action == KeyEvent.ACTION_UP && volumeDownHeld) {
   val sent = OrbitIme.shift(false)
   volumeDownHeld = false
   EventLog.recordStatus("ORBIT release accepted=$sent")
   return true
  }
  return volumeDownHeld
 }
}