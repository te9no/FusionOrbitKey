package dev.teporz.fusionorbit

import android.inputmethodservice.InputMethodService
import android.os.SystemClock
import android.view.KeyEvent

class OrbitIme : InputMethodService() {
 companion object {
  @Volatile private var instance: OrbitIme? = null
  @Volatile private var held = false
  private var downTime = 0L

  @Synchronized fun shift(down: Boolean): Boolean {
   val ime = instance ?: run { EventLog.recordStatus("IME unavailable"); return false }
   val connection = ime.currentInputConnection ?: run {
    EventLog.recordStatus("IME has no InputConnection")
    return false
   }
   if (down == held) return true
   val now = SystemClock.uptimeMillis()
   if (down) downTime = now
   val event = KeyEvent(downTime, now,
    if (down) KeyEvent.ACTION_DOWN else KeyEvent.ACTION_UP,
    KeyEvent.KEYCODE_SHIFT_LEFT, 0)
   val sent = connection.sendKeyEvent(event)
   EventLog.recordStatus("IME Shift ${if (down) "DOWN" else "UP"} sent=$sent")
   if (sent) held = down
   return sent
  }
  @Synchronized fun release() { if (held) shift(false) }
 }
 override fun onCreate() {
  super.onCreate()
  instance = this
  EventLog.recordStatus("IME created")
 }
 override fun onFinishInput() {
  release()
  super.onFinishInput()
 }
 override fun onDestroy() {
  release()
  instance = null
  super.onDestroy()
 }
}