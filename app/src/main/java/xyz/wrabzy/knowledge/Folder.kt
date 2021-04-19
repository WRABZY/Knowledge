package xyz.wrabzy.knowledge

import java.util.ArrayList

open class Folder {

    var wayFromRoot: String = ""
    var name: String = ""
    var catalog: String = ""
    var nameToShow: String = ""
    var contentCount: Int
    var birth: Long
    var folders: MutableList<Folder> = ArrayList<Folder>()
    var articles: MutableList<Article> = ArrayList<Article>()

    constructor(relativeURL: String) {
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
                    articles.add(Article("$relativeURL/$articleName"))
                }
            }
        }.start()
    }

    constructor(_folders: MutableList<Folder> = ArrayList<Folder>(),
                 _articles: MutableList<Article> = ArrayList<Article>(),
                 _contentCount: Int,
                 _birth: Long) {
        folders = _folders
        articles = _articles
        contentCount = _contentCount
        birth = _birth
    }



    var filesLoaded: Int = 0





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

    fun getFolder(index: Int, urls: ArrayList<String>): Folder {

        var nextFolder: Folder = this
        for (folder in folders) {
            if (urls[index] == folder.name) {
                nextFolder = folder
            }
        }
        return if (index == urls.size - 1) nextFolder
        else nextFolder.getFolder(index + 1, urls)
    }
}