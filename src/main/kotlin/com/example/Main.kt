package com.example

import org.apache.commons.compress.archivers.zip.*
import java.io.File
import java.util.*

internal var allFilesPredicate: ZipArchiveEntryPredicate = ZipArchiveEntryPredicate { true }

fun main(args: Array<String>) {
    val kobalt = File("kobalt-1.0.58.jar")
    val zip = File("kobalt-1.0.58.zip")
    val src = File("kobalt-1.0.58-sources.jar")

    //rezip2(kobalt, zip)
    rezip3(kobalt, src, zip)
}

fun rezip(jarIn: File, zipOut: File) {
    val s = System.currentTimeMillis()
    val zos = ZipArchiveOutputStream(zipOut)
    zos.encoding = "UTF-8"
    val zip = ZipFile(jarIn)
    zip.copyRawEntries(zos, allFilesPredicate)
    zos.close()
    zip.close()

    println("Rezip Time: " + (System.currentTimeMillis() - s) + "ms")
}

fun rezip2(jarIn: File, zipOut: File) {
    val s = System.currentTimeMillis()
    val zos = ZipArchiveOutputStream(zipOut)
    zos.encoding = "UTF-8"
    val zip = ZipFile(jarIn)

    for (entry in zip.entries) {
        zos.addRawArchiveEntry(entry, zip.getRawInputStream(entry))
    }

    zos.close()

    zip.close()

    println("Rezip2 Time: " + (System.currentTimeMillis() - s) + "ms")
}

fun rezip3(jarIn: File, srcJar: File, zipOut: File) {
    val s = System.currentTimeMillis()
    val zos = ZipArchiveOutputStream(zipOut)
    zos.encoding = "UTF-8"
    val jar = ZipFile(jarIn)
    
    val jarEntries = jar.entries

    for (entry in jar.entries) {
        zos.addRawArchiveEntry(entry, jar.getRawInputStream(entry))
    }

    jar.close()

    val src = ZipFile(srcJar)

    for (entry in src.entries) {
        if (!entryExists(jarEntries, entry)) {
            zos.addRawArchiveEntry(entry, src.getRawInputStream(entry))
        }
    }

    src.close()

    zos.close()

    println("Rezip3 Time: " + (System.currentTimeMillis() - s) + "ms")
}

fun entryExists(jarEntries: Enumeration<ZipArchiveEntry>, entry: ZipArchiveEntry): Boolean {
    for (e in jarEntries) {
        if (e.name == entry.name) {
            return true
        }
    }
    return false
}

