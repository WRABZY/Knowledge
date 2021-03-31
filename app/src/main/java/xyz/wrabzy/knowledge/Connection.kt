package xyz.wrabzy.knowledge

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.UnknownHostException

class Connection(private val relativeAddress: String = Storage.core) {

    private val address: URL = URL("${Storage.base}/${Storage.core}/$relativeAddress.html" )

    private val logTag = "Connection"

    fun getContent(): String {

        val input by lazy { BufferedReader(InputStreamReader(address.openStream())) }
        var opened = false

        val page = StringBuilder()

        try {

            var line: String?
            do {
                line = input.readLine()
                opened = true
                page.append(line)
            } while (line != null)

        } catch (uhe: UnknownHostException) {

            Log.d(logTag, uhe.toString())

        } finally {

            if (opened) input.close()

        }

        return page.toString()
    }

}
/*
fun main(args: Array<String>) {
    val test = Connection()
    val pair = test.foldersFiles()
    for ((k, v) in pair.first) {
        println("$k = $v")
    }
    println()
    for ((k, v) in pair.second) {
        println("$k = $v")
    }
    println()
}*/


/*
    fun foldersFiles(address: URL = this.address): Pair<Map<String, Int>, Map<String, Int>> {




    }

    fun extractFoldersFilesFromPage(page: StringBuilder): Pair<Map<String, Int>, Map<String, Int>> {
        val folders = page.substring(page.indexOf("<ul>") + 4, page.indexOf("</ul>"))
        val files = page.substring(page.indexOf("<ol>") + 4, page.indexOf("</ol>"))

        val foldersMap: MutableMap<String, Int> = mutableMapOf()
        val foldersArray = folders.split("<li>")
        for (string in foldersArray) {
            if (string.contains("</li>"))
                foldersMap.put(
                    string.substring(0, string.indexOf("<mark>")),
                    Integer.parseInt(string.substring(string.indexOf("<mark>") + 6, string.indexOf("</mark>")))
                )
        }

        val filesMap: MutableMap<String, Int> = mutableMapOf()
        val filesArray = files.split("<li>")
        for (string in filesArray) {
            if (string.contains("</li>"))
                filesMap.put(
                    string.substring(0, string.indexOf("<mark>")),
                    Integer.parseInt(string.substring(string.indexOf("<mark>") + 6, string.indexOf("</mark>")))
                )
        }

        if (foldersMap.isEmpty()) foldersMap["empty"] = -1
        if (filesMap.isEmpty()) filesMap["empty"] = -1

        return foldersMap to filesMap
    }

*/