package xyz.wrabzy.knowledge

class Article (val relativeURL: String) {

    val wayFromRoot: String
    val name: String
    val nameToShow: String
    val content: String
    val birth: Long

    init {
        val lastIndexOfSlash = relativeURL.lastIndexOf("/")
        wayFromRoot = if (lastIndexOfSlash != -1) relativeURL.substring(0, lastIndexOfSlash) else Home.address()
        name = relativeURL.substring(lastIndexOfSlash + 1)
        content = InitController.downloadFile(relativeURL).second
        val extractor = Extractor.ofArticle(content)
        nameToShow = extractor.fileNameToShow
        birth = extractor.birth
    }

    override fun toString(): String {
        return "File: $name ($relativeURL), last update: $birth\n"
    }
}