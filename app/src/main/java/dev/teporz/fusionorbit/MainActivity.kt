package dev.teporz.fusionorbit
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
class MainActivity : Activity() {
 private lateinit var output: TextView
 private val refresh: () -> Unit = { runOnUiThread { output.text = EventLog.lines().ifBlank { "No key events yet. Press Volume Down while Windows App is foreground." } } }
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24,24,24,24) }
  fun button(label: String, onClick: () -> Unit) { root.addView(Button(this).apply { text = label; setOnClickListener { onClick() } }) }
  root.addView(TextView(this).apply { text = "Fusion Orbit Key — Gboard-Compatible Diagnostic v0.5.0\n\nKeep Gboard selected. Enable Accessibility service. In Windows App, press and release Volume Down, then inspect the logs. Shift injection is not yet available. No input method changes required."; textSize = 16f })
  button("Open Accessibility Settings") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
  button("Copy log") { (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("Fusion Orbit Key events", EventLog.lines())) }
  button("Clear log") { EventLog.clear() }
  val scroll = ScrollView(this)
  output = TextView(this).apply { textSize = 13f; setTextIsSelectable(true); setPadding(8,8,8,8) }
  scroll.addView(output)
  root.addView(scroll, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1f))
  setContentView(root)
  refresh()
 }
 override fun onResume() { super.onResume(); EventLog.subscribe(refresh); refresh() }
 override fun onPause() { EventLog.unsubscribe(refresh); super.onPause() }
 override fun dispatchKeyEvent(event: KeyEvent): Boolean {
  EventLog.record("ACTIVITY",event)
  return super.dispatchKeyEvent(event)
 }
}