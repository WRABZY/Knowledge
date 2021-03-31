package xyz.wrabzy.knowledge

import java.lang.NumberFormatException

class Extractor {
    companion object {

        fun getFolders(page: String): List<String>? {
            return getLines(page, "ul")
        }

        fun getArticles(page: String): List<String>? {
            return getLines(page, "ol")
        }

        private fun getLines(page: String, tag: String): List<String>? {

            val helpFulPart = extract(page, "<$tag>", "</$tag>")

            return if (helpFulPart.isEmpty()) null else helpFulPart.split("<li>")

        }

        fun getBirth(page: String): Long = try {
                                               extract(page, "<div class=\"UT\">", "</div>").toLong()
                                           } catch (nfe: NumberFormatException) {
                                               -1
                                           }


        fun getName(page: String, lang: String): String = extract(page, "<h2 id=\"$lang\">", "</h2>").replace("&lt;", "<")
                                                                                                               .replace("&gt;", ">")

        fun getArticleName(page: String): String = extract(page, "<h1>", "</h1>").replace("&lt;", "<")
                                                                                           .replace("&gt;", ">")

        fun getArticleContent(page: String): String = extract(page, "</h1>", "</body>")

        private fun extract(page: String, from: String, to: String): String = page.substring( page.indexOf(from) + from.length, page.indexOf(to) )

        fun getFolderUrlFromLine(line: String) = line.substring(0, line.indexOf("<mark>"))

        fun getFolderContentCount(line: String) = line.substring(line.indexOf("<mark>") + 6, line.indexOf("</mark>")).toInt()

        fun getArticleUrlFromLine(line: String) = line.substring(0, line.indexOf(".html"))

    }
}