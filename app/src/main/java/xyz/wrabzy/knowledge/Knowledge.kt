package xyz.wrabzy.knowledge

class Knowledge(
    val folders: MutableList<Folder>?,
    val articles: MutableList<Article>?
) {
    override fun toString(): String {
        val stringBuilder = StringBuilder("Knowledge\n")

        if (!folders.isNullOrEmpty())
            for (folder in folders) {
                stringBuilder.append("\t$folder")
            }

        if (!articles.isNullOrEmpty())
            for (article in articles) {
                stringBuilder.append("\t$articles")
            }

        return stringBuilder.toString()
    }
}