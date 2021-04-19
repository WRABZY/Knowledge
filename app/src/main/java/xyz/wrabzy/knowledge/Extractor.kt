package xyz.wrabzy.knowledge

import java.util.ArrayList
import java.util.logging.*
import java.util.regex.Pattern

private val logger = Logger.getLogger("xyz.wrabzy.knowledge.Extractor")


/**
 * Helps to extract information about files and directories from their html descriptions
 */
class Extractor private constructor(
    val contentCount: Int = 0,
    val birth: Long = 0,
    val foldersURLs: ArrayList<String>? = null,
    val articlesURLs: ArrayList<String>? = null,
    val fileNameToShow: String = ""
) {

    companion object {

        /**
         * Fabric of extractor, that contains information about directory
         * */
        fun ofFolder(text: String): Extractor {
            val countFolderFile = extractCountFoldersAndFiles(text)
            return Extractor(countFolderFile.first, watchBirth(text), countFolderFile.second, countFolderFile.third)
        }

        /**
         * Fabric of extractor, that contains information about file
         * */
        fun ofArticle(text: String) = Extractor(fileNameToShow = watchArticleName(text))

        /**
         * @return count of content-files inside this directory, array of folders URLs and array of files URLs of this directory
         * */
        private fun extractCountFoldersAndFiles(text: String): Triple<Int, ArrayList<String>, ArrayList<String>> {
            logger.fine("Extracting count, folders URLs and files URLs from: [$text]")

            val foldersURLs = ArrayList<String>()
            val articlesURLs = ArrayList<String>()

            var foldersURLsReady = false
            var filesFoundInsideFolders = 0
            Thread {
                logger.fine("New thread for folders URLs extracting")
                getFolders(text).forEach {
                    logger.fine("Extracting URL of directory and her content-files count from [$it]")
                    val startIndexOfMark = it.indexOf("<mark>")
                    val endIndexOfMark = it.indexOf("</mark>")
                    if (startIndexOfMark != -1 && endIndexOfMark != -1) {
                        logger.fine("<mark>...</mark> detected, extracting...")
                        foldersURLs.add(it.substring(0, startIndexOfMark))
                        logger.fine("URL added: ${foldersURLs[foldersURLs.count() - 1]}")
                        filesFoundInsideFolders += Integer.parseInt(it.substring(startIndexOfMark + 6, endIndexOfMark))
                        logger.fine("Count of files inside folders: $filesFoundInsideFolders")
                    } else {
                        logger.fine("<mark>...</mark> not found, extracting cancelled")
                    }
                }
                foldersURLsReady = true
                logger.fine("End of the thread of folders URLs extracting")
            }.start()


            var articlesURLsReady = false
            var filesFoundInsideThisFolder = 0
            Thread {
                logger.fine("New thread for articles URLs extracting")
                getArticles(text).forEach {
                    logger.fine("Extracting URL of file from [$it]")
                    val endIndexOfDot = it.indexOf(".")
                    //articlesURLs.add(it.substring(0, endIndexOfDot))
                    articlesURLs.add(it)
                    logger.fine("URL added: ${articlesURLs[articlesURLs.count() - 1]}")
                    filesFoundInsideThisFolder++
                    logger.fine("Count of files in this folder: $filesFoundInsideThisFolder")
                }
                articlesURLsReady = true
                logger.fine("End of the thread of articles URLs extracting")
            }.start()

            while (!foldersURLsReady || !articlesURLsReady) {
                Thread.sleep(10)
            }

            logger.fine("Found totally ${filesFoundInsideFolders + filesFoundInsideThisFolder} content files: inside folders[$foldersURLs] and files of this folder: {$articlesURLs}")
            return Triple(filesFoundInsideFolders + filesFoundInsideThisFolder, foldersURLs, articlesURLs)
        }

        private fun getFolders(text: String): List<String> {

            val listItems = ArrayList<String>()
            logger.fine("Extracting lines with folders URLs from $text")

            val helpfulPart = extract(text, "<ul>", "</ul>")
            logger.fine("Block of lines with folders URLs: $helpfulPart")

            val matcher = Pattern.compile("((?<=<li>)[a-zA-Z0-9;&_]+<mark>\\d+</mark>(?=</li>))").matcher(helpfulPart)
            while (matcher.find()) {
                listItems.add(matcher.group().trim())
            }

            logger.fine("Founds next lines with information about folders: $listItems")
            return listItems
        }

        private fun getArticles(text: String): List<String> {
            val listItems = ArrayList<String>()
            logger.fine("Extracting lines with articles URLs from $text")

            val helpFulPart = extract(text, "<ol>", "</ol>")
            logger.fine("Block of lines with articles URLs: $helpFulPart")

            val matcher = Pattern.compile("((?<=<li>)[a-zA-Z0-9;& _]+\\.html<mark>\\d+</mark>(?=</li>))").matcher(helpFulPart)
            while (matcher.find()) {
                listItems.add(matcher.group().trim())
            }

            logger.fine("Founds next lines with information about articles: $listItems")
            return listItems
        }


        private fun extract(text: String, from: String, to: String): String {
            logger.fine("Extracting text between $from and $to from $text")
            val indexOfOpening = text.indexOf(from)
            val indexOfClosing = text.indexOf(to)
            return if (indexOfOpening != -1 && indexOfClosing != -1 && indexOfOpening < indexOfClosing) {
                val extracted = text.substring(indexOfOpening + from.length, indexOfClosing)
                logger.fine("Extracted: $extracted")
                extracted
            } else {
                logger.fine("Extracted empty string")
                ""
            }
        }

        private fun watchBirth(text: String) = try {
            extract(text, "<meta class=\"UT\">", "</meta>").toLong()
        } catch (nfe: NumberFormatException) {
            0
        }

        private fun watchArticleName(text: String): String {
            val maybeName = extract(text, "<h1>", "</h1>")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
            return if (maybeName.isNotEmpty()) {
                logger.fine("Article's name is $maybeName")
                maybeName
            }  else {
                logger.fine("Article's name is unnamed file")
                "unnamed file"
            }
        }

    }
}

fun main() {
    logger.level = Level.FINE
    logger.useParentHandlers = false
    val handler = ConsoleHandler()
    handler.level = Level.FINE
    val formatter = object : SimpleFormatter() {
        override fun format(record: LogRecord): String = "${record.loggerName} /${record.level}/ ${String.format("%tH:%<tM:%<tS", record.millis)}: ${record.message}\n"
    }
    handler.formatter = formatter
    logger.addHandler(handler)
/*
    Extractor.ofFolder(
            """TEST FOLDER
                |<ul>
                |  <li>invisible_folder</li>
                |  <li>mistakenly_invisible_folder</mark>100<mark></li>
                |  <li>visible_folder_with_3_files<mark>3</mark></li>
                |</ul>
                |<ol>
                |  <li>visible_file_1.html</li>
                |  <li>visible_file_2.html</li>
                |</ol>
            """.trimMargin())
*/
/*
    Extractor.ofArticle(
            """<h1>TEST FILE</h1> 
                |<html>
                |<div>
                |  <li>list 1</li>
                |  <li>list 2</li>
                |  <li>list 3</li>
                |  <li>list 4</li>
                |  <li>list 5</li>
                |</div>
                |</html>
            """.trimMargin())
*/
}