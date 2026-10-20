package dev.teporz.fusionorbit

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class KeyProbeService : AccessibilityService() {
 override fun onServiceConnected() {
  super.onServiceConnected()
  val info = serviceInfo
  info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
  serviceInfo = info
  EventLog.recordStatus("ACCESSIBILITY connected; filterRequested=" +
    ((serviceInfo.flags and AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS) != 0))
 }
 override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
 override fun onInterrupt() { EventLog.recordStatus("ACCESSIBILITY interrupted") }
 override fun onDestroy() {
  EventLog.recordStatus("ACCESSIBILITY destroyed")
  super.onDestroy()
 }
 override fun onKeyEvent(event: KeyEvent): Boolean {
  EventLog.record("ACCESSIBILITY", event)
  Log.d("FusionOrbitKey", "onKeyEvent key=${event.keyCode} action=${event.action}")
  return false
 }
}