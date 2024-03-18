package com.example.notzeroranger.database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.provider.BaseColumns

private const val authority = "com.example.notzeroranger"
private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI(authority, "users", 1)
    addURI(authority,"user/#",2)
}
class MyContentProvider : ContentProvider() {
    private lateinit var dbHelper: DemoDbHeper
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase

        val row: Long = db.insert(HighScore.PlayerEntry.TABLE_NAME, "", values)
        if (row > 0) {
            val newUri = ContentUris.withAppendedId(
                Uri.parse("content://$authority/users"),row)
            context!!.contentResolver.notifyChange(newUri, null)
            return newUri
        }
        throw SQLException("Fail to add a rew record into $uri")
    }
    override fun onCreate(): Boolean {
        dbHelper = DemoDbHeper(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val sqlBuilder = SQLiteQueryBuilder()
        sqlBuilder.tables = HighScore.PlayerEntry.TABLE_NAME

        val cursor = sqlBuilder.query(
            dbHelper.readableDatabase,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}
object HighScore {
    object PlayerEntry : BaseColumns {
        const val TABLE_NAME = "players"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_EMAIL = "score"
    }
}
private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${HighScore.PlayerEntry.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${HighScore.PlayerEntry.COLUMN_NAME_NAME} TEXT," +
            "${HighScore.PlayerEntry.COLUMN_NAME_EMAIL}, TEXT)"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${HighScore.PlayerEntry.TABLE_NAME}"



class DemoDbHeper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION, null) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "scores.db"
    }

}
