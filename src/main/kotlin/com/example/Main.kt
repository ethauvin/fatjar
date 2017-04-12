package com.example

import org.apache.commons.compress.archivers.zip.*
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.system.measureTimeMillis

internal var allFilesPredicate: ZipArchiveEntryPredicate = ZipArchiveEntryPredicate { true }
internal val MANIFEST = "MANIFEST.MF"

fun main(args: Array<String>) {
    val kobalt = File("kobalt-1.0.58.jar")
    val zip = File("kobalt-1.0.58.zip")
    val src = File("kobalt-1.0.58-sources.jar")

    //reZip2(kobalt, zip)
    reZip3(kobalt, src, zip)
}

// Straight raw copy, not very flexible
fun reZip(jarIn: File, zipOut: File) {
    val time = measureTimeMillis {
        val zos = ZipArchiveOutputStream(zipOut).apply { encoding = "UTF-8" }
        val zip = ZipFile(jarIn)

        zip.copyRawEntries(zos, allFilesPredicate)

        zos.close()
        zip.close()
    }

    println("ReZip Time: $time ms")
}

// Raw copy: jar -> zip
fun reZip2(jarIn: File, zipOut: File) {
    val time = measureTimeMillis {
        val zos = ZipArchiveOutputStream(zipOut).apply { encoding = "UTF-8" }
        val zip = ZipFile(jarIn)

        for (entry in zip.entries) {
            zos.addRawArchiveEntry(entry, zip.getRawInputStream(entry))
        }

        zos.close()
        zip.close()
    }

    println("ReZip-2 Time: $time ms")
}

// Raw copy: jar x 2 -> zip
fun reZip3(jarIn: File, srcJar: File, zipOut: File) {
    val time = measureTimeMillis {
        val zos = ZipArchiveOutputStream(zipOut).apply { encoding = "UTF-8" }
        val jar = ZipFile(jarIn)

        // get the jar entries
        val jarEntries = jar.entries

        // copy the entries
        for (entry in jar.entries) {
            if (!entry.name.endsWith(MANIFEST)) {
                zos.addRawArchiveEntry(entry, jar.getRawInputStream(entry))
            }
        }

        jar.close()

        // get the src jar entries
        val src = ZipFile(srcJar)

        // copy the entries, no dups
        for (entry in src.entries) {
            if (!entryExists(jarEntries, entry)) {
                zos.addRawArchiveEntry(entry, src.getRawInputStream(entry))
            }
        }

        val tmp = Files.createTempFile(MANIFEST, ".tmp").toFile()
        tmp.writeText("Manifest-Version: 1.0\r\nMain-Class: com.beust.kobalt.MainKt\r\n")

        val entry = zos.createArchiveEntry(tmp, "META-INF/$MANIFEST")
        zos.putArchiveEntry(entry)
        tmp.inputStream().use { ins ->
            ins.copyTo(zos, 50 * 1024)
        }
        zos.closeArchiveEntry()

        src.close()
        zos.close()
    }

    println("ReZip-3 Time: $time ms")
}

// Look for duplicate entries
fun entryExists(jarEntries: Enumeration<ZipArchiveEntry>, entry: ZipArchiveEntry): Boolean {
    for (e in jarEntries) {
        if (e.name.endsWith(MANIFEST)) {
            return true
        }
        if (e.name == entry.name) {
            return true
        }
    }
    return false
}

