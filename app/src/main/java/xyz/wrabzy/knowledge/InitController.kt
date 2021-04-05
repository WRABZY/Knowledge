package xyz.wrabzy.knowledge

import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL
import java.net.UnknownHostException
import java.util.logging.*

private val logger = Logger.getLogger("xyz.wrabzy.knowledge.InitController")



/**
 * This class is the node of the application loading system.
 * Many methods of this system manipulate objects of different classes.
 * To eliminate dependencies and rigid connections between classes,
 * all methods using objects of various components of the loading system are brought into this class,
 * so this class is, as it were, the center of the information loading system.
 * */
class InitController {

    companion object {

        /**
         * Trying to download a file
         * @return an array of exceptions that were thrown when trying to download a file, or a string representation of the file
         * */
        fun downloadFile(relativeAddress: String): Pair<ArrayList<Throwable>, String> {
            logger.fine("entering String[$relativeAddress]")

            val address = URL("${Home.address()}/$relativeAddress")
            logger.fine("Downloading file ${Home.address()}/$relativeAddress")

            val input by lazy { BufferedReader(InputStreamReader(address.openStream())) }
            var opened = false

            val file = StringBuilder()
            val exceptions = ArrayList<Throwable>()

            try {
                input.lines().map { StringBuilder(it) }.reduce (file) { s1, s2 -> s1.append(s2) }
                logger.fine("Downloading successful! Input stream was opened.")

                opened = true
            } catch (uhe: UnknownHostException) {

                logger.fine("Downloading failed: unknown host exception")
                exceptions.add(uhe)

            } catch (fnf: FileNotFoundException) {

                logger.fine("Downloading failed: file not found exception")
                exceptions.add(fnf)

            } finally {

                if (opened) {
                    input.close()
                    logger.fine("Input stream was closed.")
                }

            }

            val answer = file.toString()
            logger.fine("return Array$exceptions and String[$answer]")

            return Pair(exceptions, answer)
        }


        /**
         * Creating of an object containing initial information for further loading
         * @return object of this class
         * */
        fun seed(): Seed {
            logger.fine("Seed making from ${Home.root}.html")

            val rootFile = downloadFile("${Home.root}.html")

            return if (rootFile.first.size == 0) { // no exceptions
                logger.fine("Seed-file downloaded successful")

                val extractor = Extractor.ofFolder(rootFile.second)

                logger.fine("The end of seed making")
                Seed(rootFile.first,
                        extractor.foldersURLs ?: ArrayList(),
                        extractor.articlesURLs ?: ArrayList(),
                        extractor.contentCount,
                        extractor.birth)
            } else {
                logger.fine("Seed-file download failed")
                Seed(rootFile.first)
            }
        }


        var filesLoaded = 0
        fun growUp(seed: Seed, base: Knowledge) {

            Thread {
                var tryings = 0
                Thread {
                    while (filesLoaded != seed.contentCount && tryings <= 27) { // 3 seconds of unsuccessful tryings
                        Thread.sleep(111) // nine tryings in second
                        var filesInFolders = 0
                        if (base.folders.isNotEmpty()) {
                            for (folder in base.folders) filesInFolders += folder.filesLoaded
                        }
                        var numberOfArticles = 0
                        if (base.articles.isNotEmpty()) {
                            for (article in base.articles) numberOfArticles += 1
                        }
                        val filesLoadedNow = filesInFolders + numberOfArticles
                        if (filesLoadedNow > filesLoaded) {
                            filesLoaded = filesLoadedNow
                            tryings = 0
                        } else {
                            tryings++
                        }
                    }
                }.start()

                if (!seed.foldersURLs.isNullOrEmpty()) {
                    for (folderURL in seed.foldersURLs) {
                        base.folders.add(Folder(folderURL))
                    }
                }

                if (!seed.articlesURLs.isNullOrEmpty()) {
                    for (articleURL in seed.articlesURLs) {
                        base.articles.add(Article(articleURL))
                    }
                }
            }.start()
        }


    }
}

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
/*
    logger.fine("Testing of method InitController.downloadFile()")
    InitController.downloadFile("not_existing")
    InitController.downloadFile("${Home.root}.html")
*/
    logger.fine("Testing of method InitController.seed()")
    val testSeed = InitController.seed()
    logger.fine("Received seed:\n$testSeed")
}