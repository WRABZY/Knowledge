package xyz.wrabzy.knowledge

import java.util.ArrayList

class Folder(val relativeURL: String) {

    val wayFromRoot: String
    val name: String
    val catalog: String
    val nameToShow: String
    val contentCount: Int
    val birth: Long

    var filesLoaded: Int = 0

    val folders: MutableList<Folder> = ArrayList<Folder>()
    val articles: MutableList<Article> = ArrayList<Article>()


    init {
        val lastIndexOfSlash = relativeURL.lastIndexOf("/")
        wayFromRoot = if (lastIndexOfSlash != -1) relativeURL.substring(0, lastIndexOfSlash) else Home.address()
        name = relativeURL.substring(lastIndexOfSlash + 1)
        catalog = InitController.downloadFile("$relativeURL/$name.html").second

        val namesFile = InitController.downloadFile("$relativeURL/${Home.name}.html")
        val tagOpeningName = namesFile.second.indexOf("<h1>")
        val tagClosingName = namesFile.second.indexOf("</h1>", tagOpeningName)
        nameToShow = namesFile.second.substring(tagOpeningName + 4, tagClosingName)

        val extractor = Extractor.ofFolder(catalog)
        contentCount = extractor.contentCount
        birth = extractor.birth
        filesLoaded = 0

        // control of loading dynamic
        Thread {

            var tryings = 0

            while (filesLoaded != contentCount && tryings <= 9) {

                Thread.sleep(333) // three tryings in second

                var filesInFolders = 0
                if (folders.isNotEmpty()) {
                    for (folder in folders) filesInFolders += folder.filesLoaded
                }

                var numberOfArticles = 0
                if (articles.isNotEmpty()) {
                    for (article in articles) numberOfArticles += 1
                }

                val filesLoadedNow = filesInFolders + numberOfArticles
                if (filesLoadedNow > filesLoaded) {
                    filesLoaded = filesLoadedNow
                } else {
                    tryings++
                }
            }

        }.start()


        Thread {
            val folderURLs = extractor.foldersURLs
            if (folderURLs != null) {
                for (folderURL in folderURLs) {
                    folders.add(Folder("$relativeURL/$folderURL"))
                }
            }
        }.start()

        Thread {
            val articleNames = extractor.articlesURLs
            if (articleNames != null) {
                for (articleName in articleNames) {
                    articles.add(Article("$relativeURL/$articleName.html"))
                }
            }
        }.start()
    }

    fun filesLoaded() = filesLoaded

    override fun toString(): String {
        val stringBuilder: StringBuilder  = StringBuilder("Folder $nameToShow:\n")
        stringBuilder.append("\tfrom: $wayFromRoot;\n")
        stringBuilder.append("\tdirty name: $name;\n")
        stringBuilder.append("\tCount of files inside: $contentCount;\n")
        if (folders.isNotEmpty()) stringBuilder.append("\tList of folders: ${getFoldersNames()};\n")
        if (articles.isNotEmpty()) stringBuilder.append("\tList of articles: ${getArticlesNames()};\n")
        stringBuilder.append("\tLast update: $birth.\n")
        return stringBuilder.toString()
    }

    fun getFoldersNames(): String {
        val foldersNames = StringBuilder()
        if (folders.isNotEmpty()) {
            for (folder in folders) {
                foldersNames.append(folder.nameToShow)
                foldersNames.append(", ")
            }
            foldersNames.delete(foldersNames.length - 2, foldersNames.length)
        }
        return foldersNames.toString()
    }

    fun getArticlesNames(): String {
        val articlesNames = StringBuilder()
        if (articles.isNotEmpty()) {
            for (article in articles) {
                articlesNames.append(article.nameToShow)
                articlesNames.append(", ")
            }
            articlesNames.delete(articlesNames.length - 2, articlesNames.length)
        }
        return articlesNames.toString()
    }
}