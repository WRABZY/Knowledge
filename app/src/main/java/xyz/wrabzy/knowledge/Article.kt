package xyz.wrabzy.knowledge

import android.util.Log

class Article (relativeURLwithBirth: String) {

    val wayFromRoot: String
    val name: String
    val nameToShow: String
    val content: String
    val birth: Long
    val relativeURL: String

    init {
        val indexOfMark = relativeURLwithBirth.indexOf("<mark>")
        if (indexOfMark != -1) {
            relativeURL = relativeURLwithBirth.substring(0, indexOfMark)
            birth = try {
                relativeURLwithBirth.substring(indexOfMark + 6, relativeURLwithBirth.indexOf("</mark>")).toLong()
            } catch (nfe: NumberFormatException) {
                0L
            }
        } else {
            relativeURL = relativeURLwithBirth
            birth = 0L
        }
        val lastIndexOfSlash = relativeURL.lastIndexOf("/")
        wayFromRoot = if (lastIndexOfSlash != -1) relativeURL.substring(0, lastIndexOfSlash) else Home.address()
        name = relativeURL.substring(lastIndexOfSlash + 1)
        content = InitController.downloadFile(relativeURL).second
        val extractor = Extractor.ofArticle(content)
        nameToShow = extractor.fileNameToShow
    }

    override fun toString(): String {
        return "File: $name ($relativeURL), last update: $birth\n"
    }
}