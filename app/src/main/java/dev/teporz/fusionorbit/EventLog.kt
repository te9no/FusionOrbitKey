package dev.teporz.fusionorbit
import android.view.KeyEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
object EventLog {
 private val entries = ArrayDeque<String>()
 private val observers = mutableSetOf<() -> Unit>()
 @Synchronized fun record(source: String, event: KeyEvent) {
  val action = when(event.action) { KeyEvent.ACTION_DOWN -> "DOWN"; KeyEvent.ACTION_UP -> "UP"; else -> event.action.toString() }
  val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
  entries.addLast("$time [$source] $action key=${event.keyCode} (${KeyEvent.keyCodeToString(event.keyCode)}) scan=${event.scanCode} device=${event.deviceId} repeat=${event.repeatCount} flags=${event.flags}")
  while(entries.size > 300) entries.removeFirst()
  observers.toList().forEach { it() }
 }
 @Synchronized fun lines(): String = entries.joinToString("\n")
 @Synchronized fun clear() { entries.clear(); observers.toList().forEach { it() } }
 @Synchronized fun subscribe(observer: () -> Unit) { observers.add(observer) }
 @Synchronized fun unsubscribe(observer: () -> Unit) { observers.remove(observer) }
}