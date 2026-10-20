package dev.teporz.fusionorbit
import android.view.KeyEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
object EventLog {
 private val entries = ArrayDeque<String>()
 private val observers = mutableSetOf<() -> Unit>()
 private fun timestamp() = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
 @Synchronized fun record(source: String, event: KeyEvent) {
  val action = when(event.action) { KeyEvent.ACTION_DOWN -> "DOWN"; KeyEvent.ACTION_UP -> "UP"; else -> event.action.toString() }
  entries.addLast("${timestamp()} [$source] $action key=${event.keyCode} (${KeyEvent.keyCodeToString(event.keyCode)}) scan=${event.scanCode} device=${event.deviceId} repeat=${event.repeatCount} flags=${event.flags}")
  trimAndNotify()
 }
 @Synchronized fun recordStatus(message: String) {
  entries.addLast("${timestamp()} [SERVICE] $message")
  trimAndNotify()
 }
 private fun trimAndNotify() {
  while(entries.size > 300) entries.removeFirst()
  observers.toList().forEach { it() }
 }
 @Synchronized fun lines(): String = entries.joinToString("\n")
 @Synchronized fun clear() { entries.clear(); observers.toList().forEach { it() } }
 @Synchronized fun subscribe(observer: () -> Unit) { observers.add(observer) }
 @Synchronized fun unsubscribe(observer: () -> Unit) { observers.remove(observer) }
}