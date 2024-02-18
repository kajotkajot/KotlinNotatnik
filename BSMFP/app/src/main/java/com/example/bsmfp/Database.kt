package com.example.bsmfp

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.toxicbakery.bcrypt.Bcrypt

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "Notepad7"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Notepad"
        const val ID = "_id"
        const val LOGIN = "login"
        const val PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $LOGIN TEXT, $PASSWORD TEXT);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    @SuppressLint("Range")
    fun login(login: String, password: String): Boolean {
        val db = this.readableDatabase

        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(PASSWORD),
            "$LOGIN = ?",
            arrayOf(login),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndex("password"))
            if (Bcrypt.verify(password, storedPassword.toByteArray())) {
                cursor.close()
                db.close()
                return true
            }
        }
        cursor.close()
        db.close()
        return false
    }
}