package com.example.bsmfp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class NotepadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        val cryptoManager = KeyStoreManager()

        val noteText : EditText = findViewById(R.id.etNote)
        val noteButton : Button = findViewById(R.id.btnSave)

        val file = File(filesDir, "note.txt")

        val decryptedNote = cryptoManager.decrypt(FileInputStream(file))
        noteText.setText(Base64.encodeToString(decryptedNote, Base64.DEFAULT))

        noteButton.setOnClickListener {
            val encryptedNote = cryptoManager.encrypt(noteText.text.toString().encodeToByteArray(), FileOutputStream(file))
            noteText.setText(Base64.encodeToString(encryptedNote, Base64.DEFAULT))
        }
    }
}