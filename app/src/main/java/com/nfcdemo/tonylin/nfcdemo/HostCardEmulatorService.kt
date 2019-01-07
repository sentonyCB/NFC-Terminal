package com.nfcdemo.tonylin.nfcdemo

import android.app.Service
import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import org.apache.commons.codec.binary.Hex
import java.math.BigInteger
import kotlin.text.Charsets.UTF_8

class HostCardEmulatorService : HostApduService() {

    private var requestedAmount: Int = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extras = intent?.extras
        extras?.let {
            requestedAmount = it.getString("amount")?.toInt() ?: requestedAmount
        }
        return Service.START_STICKY
    }

    override fun onDeactivated(p0: Int) {
        Log.d(TAG, "Deactivated$p0")
    }


    //this is what runs when a request is received from the "card"
    override fun processCommandApdu(commandApdu: ByteArray?, p1: Bundle?): ByteArray {
        if (commandApdu == null) {
            return hexStringToByteArray(STATUS_FAILED)
        }

        val hexCommandApdu = toHex(commandApdu)
        if (hexCommandApdu.length < MIN_APDU_LENGTH) return hexStringToByteArray(STATUS_FAILED)

        if (hexCommandApdu.substring(0, 2) != DEFAULT_CLA) return hexStringToByteArray(CLA_NOT_SUPPORTED)

        if (hexCommandApdu.substring(2, 4) != SELECT_INS) return hexStringToByteArray(INS_NOT_SUPPORTED)

        if (hexCommandApdu.substring(10, 24) == AID && hexCommandApdu.substring(24).isNotEmpty())  {
            //this runs when the request info is of the correct form
            val availableAmount = String(Hex.decodeHex(hexCommandApdu.substring(24).toCharArray())).toInt()
            if (availableAmount >= requestedAmount) {
                return hexStringToByteArray(String.format("%x", BigInteger(1, (availableAmount - requestedAmount).toString().toByteArray(UTF_8))))
            }
            return hexStringToByteArray(String.format("%x", BigInteger(1, requestedAmount.toString().toByteArray(UTF_8))))
        } else {
            return hexStringToByteArray(STATUS_FAILED)
        }
    }

    companion object {
        val TAG = "Host Card Emulator"
        val STATUS_FAILED = "6F00"
        val CLA_NOT_SUPPORTED = "6E00"
        val INS_NOT_SUPPORTED = "6D00"
        val AID = "A0000002471001"
        val SELECT_INS = "A4"
        val DEFAULT_CLA = "00"
        val MIN_APDU_LENGTH = 12
    }
}