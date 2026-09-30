package com.rohan.offlineresearch

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Locale

/** Persistent local lexical index. Uses Android's bundled SQLite; no network or external service. */
class CorpusIndex(context: Context) : SQLiteOpenHelper(context, "research_index.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE docs(id TEXT PRIMARY KEY, title TEXT NOT NULL, kind TEXT NOT NULL, text TEXT NOT NULL)")
        db.execSQL("CREATE VIRTUAL TABLE docs_fts USING fts4(id, title, kind, text, notindexed=kind)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun rebuild(corpus: List<CorpusDoc>) {
        writableDatabase.beginTransaction()
        try {
            writableDatabase.delete("docs", null, null)
            writableDatabase.delete("docs_fts", null, null)
            corpus.forEach { d ->
                writableDatabase.execSQL("INSERT INTO docs(id,title,kind,text) VALUES(?,?,?,?)", arrayOf(d.id,d.title,d.kind,d.text))
                writableDatabase.execSQL("INSERT INTO docs_fts(id,title,kind,text) VALUES(?,?,?,?)", arrayOf(d.id,d.title,d.kind,d.text))
            }
            writableDatabase.setTransactionSuccessful()
        } finally { writableDatabase.endTransaction() }
    }

    fun search(query: String, limit: Int = 12): List<CorpusDoc> {
        val clean = query.trim().split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.length >= 2 }.joinToString(" ") { "\"${it.replace("\"", "") }*\"" }
        if (clean.isBlank()) return emptyList()
        val out = mutableListOf<CorpusDoc>()
        readableDatabase.rawQuery(
            "SELECT d.id,d.title,d.kind,d.text FROM docs_fts f JOIN docs d ON d.id=f.id WHERE docs_fts MATCH ? LIMIT ?",
            arrayOf(clean, limit.toString())
        ).use { c ->
            while (c.moveToNext()) out += CorpusDoc(c.getString(0),c.getString(1),c.getString(2),c.getString(3))
        }
        return out
    }
}
