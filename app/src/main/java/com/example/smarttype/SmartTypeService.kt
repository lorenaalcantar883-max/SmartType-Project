package com.example.smarttype

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*
import kotlin.random.Random

class SmartTypeService : AccessibilityService() {

    private var currentText = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        val text = intent?.getStringExtra("text") ?: ""
        currentText = ""
        typeSmartly(text)
        return START_STICKY
    }

    private fun typeSmartly(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            for (ch in text) {
                if (ch.isLetter() && Random.nextDouble() < 0.012) {
                    val wrong = ('a'..'z').random()
                    sendKey(wrong.toString())
                    delay(Random.nextLong(100, 200))
                    sendBackspace()
                    delay(Random.nextLong(150, 250))
                }

                val baseDelay = when {
                    ch.isUpperCase() -> 40L
                    ch.isWhitespace() -> 25L
                    ch in ".,!?;" -> 50L
                    else -> 20L
                }
                sendKey(ch.toString())
                delay(baseDelay + (0..20).random())
            }
        }
    }

    private fun sendKey(charStr: String) {
        val rootNode = rootInActiveWindow ?: return
        val focusedNode = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        if (focusedNode != null) {
            currentText += charStr
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, currentText)
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            focusedNode.recycle()
        }
        rootNode.recycle()
    }

    private fun sendBackspace() {
        if (currentText.isNotEmpty()) {
            currentText = currentText.substring(0, currentText.length - 1)
            val rootNode = rootInActiveWindow ?: return
            val focusedNode = rootNode.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
            if (focusedNode != null) {
                val arguments = Bundle()
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, currentText)
                focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                focusedNode.recycle()
            }
            rootNode.recycle()
        }
    }
}
