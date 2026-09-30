package com.rohan.offlineresearch

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import java.io.File
import java.security.MessageDigest

/** Validates and atomically installs a local JSON corpus. No network access. */
class CorpusManager(private val context: Context) {
    companion object { private const val MAX_CORPUS_BYTES = 2L * 1024 * 1024 * 1024 }
    private val target = File(context.filesDir, "research-corpus.json")

    fun hasImportedCorpus(): Boolean = target.isFile && target.length() > 0
    fun corpusFile(): File = target
    fun sha256(): String = sha256(target)

    fun importJson(uri: Uri): File {
        val tmp = File(context.cacheDir, "corpus-import-${System.nanoTime()}.tmp")
        try {
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "Cannot open corpus file." }
                tmp.outputStream().use { out ->
                    val buffer = ByteArray(1024 * 1024)
                    var total = 0L
                    while (true) {
                        val n = input.read(buffer)
                        if (n < 0) break
                        total += n
                        require(total <= MAX_CORPUS_BYTES) { "Corpus exceeds 2 GB limit." }
                        out.write(buffer, 0, n)
                    }
                    out.fd.sync()
                }
            }
            validate(tmp)
            if (target.exists()) require(target.delete()) { "Could not replace existing corpus." }
            check(tmp.renameTo(target)) { "Could not finalize corpus import." }
            return target
        } catch (t: Throwable) {
            tmp.delete()
            throw t
        }
    }

    fun deleteImportedCorpus() { target.delete() }

    fun loadImported(): List<CorpusDoc> {
        if (!hasImportedCorpus()) return emptyList()
        val arr = JSONArray(target.readText())
        return buildList {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                add(CorpusDoc(o.getString("id"), o.getString("title"), o.optString("kind", "reference"), o.getString("text")))
            }
        }
    }

    private fun validate(file: File) {
        val arr = JSONArray(file.readText())
        require(arr.length() > 0) { "Corpus must contain at least one document." }
        val ids = mutableSetOf<String>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val id = o.getString("id").trim()
            val title = o.getString("title").trim()
            val text = o.getString("text").trim()
            require(id.isNotEmpty() && ids.add(id)) { "Duplicate or empty corpus id at row ${i + 1}." }
            require(title.isNotEmpty()) { "Empty title at row ${i + 1}." }
            require(text.isNotEmpty()) { "Empty text at row ${i + 1}." }
        }
    }

    private fun sha256(file: File): String {
        if (!file.isFile) return ""
        val md = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buf = ByteArray(1024 * 1024)
            var n: Int
            while (input.read(buf).also { n = it } > 0) md.update(buf, 0, n)
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
