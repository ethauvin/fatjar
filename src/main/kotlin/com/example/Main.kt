package com.example

import org.apache.commons.compress.archivers.zip.*
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

internal var allFilesPredicate: ZipArchiveEntryPredicate = ZipArchiveEntryPredicate { true }

fun main(args: Array<String>) {
    val kobalt = File("kobalt-1.0.58.jar")
    val zip = File("kobalt-1.0.58.zip")
    val src = File("kobalt-1.0.58-sources.jar")

    //rezip2(kobalt, zip)
    rezip3(kobalt, src, zip)
}

// Straight raw copy, not very flexible
fun rezip(jarIn: File, zipOut: File) {
    val time = measureTimeMillis {
        val zos = ZipArchiveOutputStream(zipOut)
        zos.encoding = "UTF-8"
        val zip = ZipFile(jarIn)

        zip.copyRawEntries(zos, allFilesPredicate)

        zos.close()
        zip.close()
    }

    println("Rezip Time: $time ms")
}

// Raw copy: jar -> zip
fun rezip2(jarIn: File, zipOut: File) {
    val time = measureTimeMillis {
        val zos = ZipArchiveOutputStream(zipOut)
        zos.encoding = "UTF-8"
        val zip = ZipFile(jarIn)

        for (entry in zip.entries) {
            zos.addRawArchiveEntry(entry, zip.getRawInputStream(entry))
        }

        zos.close()

        zip.close()
    }

    println("Rezip2 Time: $time ms")
}

// Raw copy: jar x 2 -> zip
fun rezip3(jarIn: File, srcJar: File, zipOut: File) {
    val time = measureTimeMillis {
        val zos = ZipArchiveOutputStream(zipOut)
        zos.encoding = "UTF-8"
        val jar = ZipFile(jarIn)

        // get the jar entries
        val jarEntries = jar.entries

        // copy the entries
        for (entry in jar.entries) {
            zos.addRawArchiveEntry(entry, jar.getRawInputStream(entry))
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

        src.close()
        zos.close()
    }

    println("Rezip3 Time: $time ms")
}

// Look for duplicate entries
fun entryExists(jarEntries: Enumeration<ZipArchiveEntry>, entry: ZipArchiveEntry): Boolean {
    for (e in jarEntries) {
        if (e.name == entry.name) {
            return true
        }
    }
    return false
}

