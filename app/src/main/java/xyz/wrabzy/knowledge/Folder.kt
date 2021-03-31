package xyz.wrabzy.knowledge

import java.net.URL

class Folder(
    val urlPart: String,
    val name: String,
    val contentCount: Int,
    val folders: MutableList<Folder>?,
    val articles: MutableList<Article>?,
    val birth: Long
) {

    override fun toString(): String {
        val stringBuilder: StringBuilder  = StringBuilder("Folder: \n")
        stringBuilder.append("\turl: $urlPart\n")
        stringBuilder.append("\tname: $name\n")
        stringBuilder.append("\tCount of files inside: $contentCount\n")
        stringBuilder.append("\tList of folders: $folders\n")
        stringBuilder.append("\tList of articles: $articles\n")
        stringBuilder.append("\tLast update: $birth\n")

        return stringBuilder.toString()
    }
}