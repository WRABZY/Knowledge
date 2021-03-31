package xyz.wrabzy.knowledge

import android.util.Log

/**
 * Start point of loading information from internet
 * */
class Initializer(private val lang: String) {
    private val logTag = "Initializer"

    companion object {
        fun go(lang: String = "ru"): Knowledge {
            val initializer = Initializer(lang)
            val foldersAndArticles = initializer.getFoldersAndArticles("", "")
            return Knowledge(foldersAndArticles.first, foldersAndArticles.second)
        }
    }

    private fun defineFolderPage(relativeURL: String): String? {
        val connection = if (relativeURL.length == 1) Connection() else Connection(relativeURL)
        val page = connection.getContent()

        return if (page.isEmpty()) {
            null
        } else {
            page
        }
    }

    fun getFoldersAndArticles(urlPartOne: String, urlPartTwo: String): Pair<MutableList<Folder>, MutableList<Article>> {

        val page = defineFolderPage("$urlPartOne/$urlPartTwo")

        val foldersList: MutableList<Folder> = ArrayList()
        val articlesList: MutableList<Article> = ArrayList()

        if (page != null) {

            val folders: List<String>? = Extractor.getFolders(page)

            if (!folders.isNullOrEmpty()) {
                for (line in folders) {
                    if (line.contains("</li>")) {
                        val urlOfFolder: String = Extractor.getFolderUrlFromLine(line)
                        val name: String = Extractor.getName(Connection("$urlPartOne/$urlOfFolder/sname").getContent(), lang)
                        val contentCount: Int = Extractor.getFolderContentCount(line)
                        val foldersOfFolder: MutableList<Folder>? = getFoldersAndArticles("$urlPartOne/$urlOfFolder", urlOfFolder).first
                        val articlesOfFolder: MutableList<Article>? = getFoldersAndArticles("$urlPartOne/$urlOfFolder", urlOfFolder).second
                        val birth: Long = Extractor.getBirth(Connection("$urlPartOne/$urlOfFolder/$urlOfFolder").getContent())

                        foldersList.add(
                            Folder(
                                urlOfFolder,
                                name,
                                contentCount,
                                foldersOfFolder,
                                articlesOfFolder,
                                birth
                            )
                        )

                    }
                }
            }

            val articles: List<String>? = Extractor.getArticles(page)

            if (!articles.isNullOrEmpty()) {
                for (line in articles) {
                    if (line.contains("</li>")) {
                        val urlOfArticle: String = Extractor.getArticleUrlFromLine(line)

                        val articleText = Connection("$urlPartOne/$urlOfArticle").getContent()

                        val name: String = Extractor.getArticleName(articleText)
                        val content: String = articleText //Extractor.getArticleContent()
                        val birth: Long = Extractor.getBirth(articleText)

                        articlesList.add(
                            Article(
                                urlOfArticle,
                                name,
                                content,
                                birth
                            )
                        )

                    }
                }
            }



        }

        return foldersList to articlesList
    }
}

fun main() {
    println(Initializer.go())
}