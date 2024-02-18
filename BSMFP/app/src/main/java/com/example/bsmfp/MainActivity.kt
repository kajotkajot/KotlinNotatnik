package com.example.bsmfp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var tvInfo: TextView
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo
    private lateinit var intent: Intent
    private lateinit var dbHelper: DBHelper
    private var tries = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        val loginText : EditText = findViewById(R.id.etLogin)
        val passwordText : EditText = findViewById(R.id.etPassword)
        val loginButton : Button = findViewById(R.id.btnLogin)
        val notificationText : TextView = findViewById(R.id.tvNotification)

        tvInfo = findViewById(R.id.tvFingerprint)
        findViewById<AppCompatImageView>(R.id.ivFingerprint).setOnClickListener {
            checkDeviceHasBiometric()
        }
        intent = Intent(this, NotepadActivity::class.java)

        loginButton.setOnClickListener {
            if (dbHelper.login(loginText.text.toString(), passwordText.text.toString()) && tries < 5) {
                startActivity(intent)
            }else if (tries < 5){
                tries += 1
                notificationText.text = "${5 - tries} more tries left"
            }else{
                notificationText.text = "notepad blocked"
            }
        }
    }

    private fun createBiometricListener() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                tvInfo.text = "Authentication error"
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                tvInfo.text = "Authentication failed"
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                tvInfo.text = "Authentication succeeded"
                startActivity(intent)
            }
        })
    }

    private fun createPromptInfo() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using fingerprint")
            .setNegativeButtonText("Cancel biometric")
            .build()
    }

    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                tvInfo.text = "App has biometric"
                createBiometricListener()
                createPromptInfo()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                tvInfo.text = "App has no biometric"
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                tvInfo.text = "App biometric unavailable"
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                tvInfo.text = "App biometric not enabled"
            }
            else -> {
                tvInfo.text = "Something went wrong"
            }
        }
    }
}
