package xyz.wrabzy.knowledge

import java.io.FileNotFoundException
import java.lang.StringBuilder
import java.net.UnknownHostException
import java.util.logging.Logger
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.SimpleFormatter

private val logger = Logger.getLogger("xyz.wrabzy.knowledge.Seed")

/**
 * An object containing initial information for further loading
 * */
class Seed (
        val exceptions: ArrayList<Throwable>,
        val foldersURLs: ArrayList<String>  = ArrayList(),
        val articlesURLs: ArrayList<String> = ArrayList(),
        var contentCount: Int = 0,
        var lastUpdate: Long = 0
) {
    override fun toString(): String {
        val stringBuilder = StringBuilder("Seed:")

        stringBuilder.append("\n\tExceptions: ")
        if (!exceptions.isNullOrEmpty())
            for (exception in exceptions) {
                stringBuilder.append("($exception) ")
            }

        stringBuilder.append("\n\tFolders: ")

        if (!foldersURLs.isNullOrEmpty())
            for (folder in foldersURLs) {
                stringBuilder.append("[$folder] ")
            }

        stringBuilder.append("\n\tArticles: ")

        if (!articlesURLs.isNullOrEmpty())
            for (article in articlesURLs) {
                stringBuilder.append("{$article} ")
            }

        stringBuilder.append("\n\tTotal files: $contentCount")

        stringBuilder.append("\n\tLast update: ${String.format("%tY.%<tm.%<td %<tH:%<tM:%<tS", lastUpdate)}")

        stringBuilder.append("\n")

        return stringBuilder.toString()
    }
}

/**
 * Selftesting
 * */
fun main() {
    logger.level = Level.FINE
    logger.useParentHandlers = false
    val handler = ConsoleHandler()
    handler.level = Level.FINE
    val formatter =  object : SimpleFormatter() {
        override fun format(record: LogRecord): String = "${record.loggerName} /${record.level}/ ${String.format("%tH:%<tM:%<tS", record.millis)}: ${record.message}\n"
    }
    handler.formatter = formatter
    logger.addHandler(handler)

    val testExceptions = ArrayList<Throwable>(2)
    testExceptions.add(FileNotFoundException())
    testExceptions.add(UnknownHostException())
    logger.fine(Seed(testExceptions).toString())
}