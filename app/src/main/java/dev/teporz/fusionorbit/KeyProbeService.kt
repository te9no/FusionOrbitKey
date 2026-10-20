package dev.teporz.fusionorbit
import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
class KeyProbeService : AccessibilityService() {
 override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
 override fun onInterrupt() = Unit
 override fun onKeyEvent(event: KeyEvent): Boolean {
  EventLog.record("ACCESSIBILITY", event)
  return false
 }
}