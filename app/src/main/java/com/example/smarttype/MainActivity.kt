package com.example.smarttype

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnPasteWrite)
        btn.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData? = clipboard.primaryClip
            val text = clip?.getItemAt(0)?.text?.toString()

            if (!text.isNullOrEmpty()) {
                Toast.makeText(this, "Typing from clipboard...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SmartTypeService::class.java)
                intent.putExtra("text", text)
                startService(intent)
            } else {
                Toast.makeText(this, "Clipboard is empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
