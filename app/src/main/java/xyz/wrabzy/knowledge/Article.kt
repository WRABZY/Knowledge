package xyz.wrabzy.knowledge

import java.net.URL

class Article (
    val urlPart: String,
    val name: String,
    val content: String,
    val birth: Long
) {
    override fun toString(): String {
        return "File: $name ($urlPart), last update: $birth\n"
    }
}