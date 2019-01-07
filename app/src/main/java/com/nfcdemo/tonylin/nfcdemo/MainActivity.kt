package com.nfcdemo.tonylin.nfcdemo

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.nfc.NdefRecord
import android.nfc.NdefRecord.createMime
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            fireService()
        }
    }

    private fun fireService() {
        val intent = Intent(this, HostCardEmulatorService::class.java)
        intent.putExtra("amount", editText.text.toString())
        startService(intent)
    }
}
