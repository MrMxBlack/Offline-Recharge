package com.rohan.offlineresearch

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest

class ModelManager(private val context: Context) {
    companion object {
        const val DEFAULT_MODEL = "research-model.gguf"
        const val MAX_TOTAL_BYTES = 50L * 1024 * 1024 * 1024
        private const val COPY_BUFFER = 1024 * 1024
    }

    fun modelFile(): File = File(context.filesDir, DEFAULT_MODEL)
    fun hasModel(): Boolean = modelFile().isFile && isValidGguf(modelFile())
    fun sizeBytes(): Long = modelFile().takeIf(File::exists)?.length() ?: 0L

    /** Imports atomically enough for app-private storage and enforces the 50 GB budget before finalization. */
    fun importModel(uri: Uri): File {
        val target = modelFile()
        val tmp = File(context.cacheDir, "model-import-${System.nanoTime()}.tmp")
        try {
            var copied = 0L
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "Cannot open selected model file." }
                FileOutputStream(tmp).use { output ->
                    val buffer = ByteArray(COPY_BUFFER)
                    while (true) {
                        val n = input.read(buffer)
                        if (n < 0) break
                        copied += n
                        val projected = totalManagedBytes() - target.length().coerceAtLeast(0) + copied
                        require(projected <= MAX_TOTAL_BYTES) { "50 GB storage budget exceeded." }
                        output.write(buffer, 0, n)
                    }
                    output.fd.sync()
                }
            }
            require(isValidGguf(tmp)) { "Selected file is not a valid GGUF model." }
            if (target.exists()) require(target.delete()) { "Could not replace existing model." }
            check(tmp.renameTo(target)) { "Could not finalize model import." }
            return target
        } catch (e: Exception) {
            tmp.delete()
            throw e
        }
    }

    fun deleteModel() { modelFile().delete() }
    fun sha256(): String = sha256(modelFile())
    fun totalManagedBytes(): Long = folderSize(context.filesDir)

    private fun isValidGguf(file: File): Boolean {
        if (!file.isFile || file.length() < 8L) return false
        if (file.length() < 24L) return false
        FileInputStream(file).use { input ->
            val header = ByteArray(4)
            if (input.read(header) != 4 || !header.contentEquals(byteArrayOf(0x47, 0x47, 0x55, 0x46))) return false
            val version = ByteArray(4)
            if (input.read(version) != 4) return false
            val v = (version[0].toLong() and 0xff) or
                ((version[1].toLong() and 0xff) shl 8) or
                ((version[2].toLong() and 0xff) shl 16) or
                ((version[3].toLong() and 0xff) shl 24)
            return v == 2L || v == 3L
        }
    }

    private fun sha256(file: File): String {
        if (!file.isFile) return ""
        val md = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { input ->
            val buf = ByteArray(COPY_BUFFER)
            var n: Int
            while (input.read(buf).also { n = it } > 0) md.update(buf, 0, n)
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }

    private fun folderSize(file: File): Long = if (file.isFile) file.length() else file.listFiles()?.sumOf(::folderSize) ?: 0L
}
