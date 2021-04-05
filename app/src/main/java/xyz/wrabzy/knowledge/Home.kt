package xyz.wrabzy.knowledge

class Home {

    companion object {

        private const val host: String = "http://www.wrabzy.xyz"
        private const val redirect: String = "http://wrabzy.github.io"
        const val root: String = "knowledge"
        const val name: String = "sname"

        fun address() = "$host/$root"
        fun spareAddress() = "$redirect/$root" //TODO

    }
}