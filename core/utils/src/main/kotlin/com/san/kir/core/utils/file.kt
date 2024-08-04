package com.san.kir.core.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.san.kir.core.utils.coroutines.withIoContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

public val externalDir: File = android.os.Environment.getExternalStorageDirectory()

public fun getFullPath(path: String): File = File(externalDir, path)

public val File.shortPath: String
    get() = if (path.isNotEmpty()) Regex("/${DIR.ROOT}.+").find(path)!!.value else path

public val File.lengthMb: Double
    get() = bytesToMb(walkTopDown().filter(File::isFile).sumOf(File::length))

public fun bytesToMb(value: Long): Double = value.toDouble() / (1024.0 * 1024.0)

public fun getCountPagesForChapterInMemory(shortPath: String): Int {
    return getFullPath(shortPath)
        .ifExists
        ?.listFiles { _, s -> checkExtension(s) }
        ?.size ?: 0
}

private val File.ifExists: File?
    get() = if (this.exists()) this else null

private val imageExtensions: List<String> = listOf("png", "jpg", "webp", "gif")

private fun checkExtension(fileName: String): Boolean {
    return imageExtensions.any { fileName.lowercase(Locale.ROOT).endsWith(it) }
}

public val File.isEmptyDirectory: Boolean
    get() =
        if (exists() and isDirectory) {
            var isOk = false
            try {
                listFiles()?.let {
                    if (it.isEmpty())
                        isOk = true
                }
            } catch (_: NullPointerException) {
            }
            isOk
        } else
            true

public suspend fun delChapters(chapters: List<String>): ResultDeleting = delFiles(chapters)

public suspend fun delFiles(filesPath: List<String>): ResultDeleting = withIoContext {
    var acc = 0
    filesPath.forEach { path ->
        getFullPath(path).apply { if (exists() && deleteRecursively()) acc++ }
    }
    ResultDeleting(current = acc, max = filesPath.size)
}

// Проверка, что файл является корректным изображением формата PNG
public fun File.isOkPng(): Boolean {
    kotlin.runCatching {
        val bytes = this.readBytes()
        if (bytes.size < 4) return false

        if (bytes[0] != 0x89.toByte() || bytes[1] != 0x50.toByte()) return false
        if (bytes[bytes.size - 2] != 0x60.toByte() || bytes[bytes.size - 1] != 0x82.toByte()) return false
    }.onFailure {
        return false
    }

    return true
}

private const val DEFAULT_COMPRESS_QUALITY = 90

@Suppress("BlockingMethodInNonBlockingContext")
public suspend fun convertImagesToPng(image: File): File = withIoContext {
    val b = BitmapFactory.decodeFile(image.path)

    val png = File(
        image.parentFile,
        "${image.nameWithoutExtension}.png"
    )

    image.delete()

    png.createNewFile()

    if (b != null) {
        val stream = FileOutputStream(png.absoluteFile)
        b.compress(Bitmap.CompressFormat.PNG, DEFAULT_COMPRESS_QUALITY, stream)
        stream.close()
    }

    png
}
