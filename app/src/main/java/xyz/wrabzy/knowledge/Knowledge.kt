package xyz.wrabzy.knowledge

class Knowledge(
    var folders: MutableList<Folder>,
    var articles: MutableList<Article>,
    var contentCount: Int,
    var lastUpdate: Long
) {
    override fun toString(): String {
        val stringBuilder = StringBuilder("Knowledge\n")

        if (folders.isNotEmpty())
            for (folder in folders) {
                stringBuilder.append("\t$folder")
            }

        if (articles.isNotEmpty())
            for (article in articles) {
                stringBuilder.append("\t$articles")
            }

        return stringBuilder.toString()
    }

    companion object {
        var base: Knowledge = Knowledge(ArrayList<Folder>(), ArrayList<Article>(), 0, -1)
    }
}