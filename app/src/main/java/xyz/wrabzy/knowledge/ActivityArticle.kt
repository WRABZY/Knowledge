package xyz.wrabzy.knowledge

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.StringBuilder

import java.util.regex.Pattern
import java.util.Deque
import java.util.ArrayDeque

class ActivityArticle : AppCompatActivity() {

    private val ltPattern = Pattern.compile("&lt;")
    private val gtPattern = Pattern.compile("&gt;")
    private val nbspPattern = Pattern.compile("&nbsp;")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setBackgroundColor(resources.getColor(R.color.back_default, null))
        setContentView(rootLayout)

        val scrollView = ScrollView(this)
        rootLayout.addView(scrollView)

        val scrollViewLayout = LinearLayout(this)
        scrollView.addView(scrollViewLayout)
        scrollViewLayout.orientation = LinearLayout.VERTICAL

        var articleText = StringBuilder()
        articleText.append(this.intent.getStringExtra("content"))
        articleText = clearTabs(articleText)

        buildActivity(articleText, scrollViewLayout)
    }

    private fun buildActivity(text: StringBuilder, result: LinearLayout) {
        val orderStack: Deque<Int> = ArrayDeque()

        val pattern = Pattern.compile("(<\\p{L}+\\d?>|</\\p{L}+\\d?>)")
        val matcher = pattern.matcher(text)

        var first: String
        var indexAfterFirst = 0
        var second: String
        var indexOfSecond: Int
        var content: String
        var trimmedContent: String
        var ordered = false
        var firstListElem = true
        var listElem = false
        var mark = false
        var code = false
        val unorderedMarker = resources.getString(R.string.unorderedMarker)

        var textSize = resources.getDimension(R.dimen.default_text_size)
        var textTypeFace = Typeface.DEFAULT
        var textStyle = Typeface.NORMAL

        var textTopPadding = resources.getDimension(R.dimen.padding_default_top)
        var textBotPadding = resources.getDimension(R.dimen.padding_default_bottom)
        val textLeftPadding = resources.getDimension(R.dimen.padding_default_left)
        val textRightPadding = resources.getDimension(R.dimen.padding_default_right)

        var textTopMargin = resources.getDimension(R.dimen.margin_default_top)
        var textBotMargin = resources.getDimension(R.dimen.margin_default_bottom)
        val textLeftMargin = resources.getDimension(R.dimen.margin_default_left)
        val textRightMargin = resources.getDimension(R.dimen.margin_default_right)

        var backgroundColor = resources.getColor(R.color.transparency, null)
        var textColor = resources.getColor(R.color.text_default, null)

        while (matcher.find(indexAfterFirst)) {
            first = matcher.group()
            indexAfterFirst = text.indexOf(first, indexAfterFirst) + first.length
            if (matcher.find(indexAfterFirst)) {
                second = matcher.group()
                indexOfSecond = text.indexOf(second, indexAfterFirst)

                if (isOpening(first)) {
                    when (first) {
                        "<li>" -> {
                            listElem = true
                            /*if (!firstListElem) {*/
                                textTopMargin = resources.getDimension(R.dimen.margin_list_top)
                                textBotMargin = resources.getDimension(R.dimen.margin_default_bottom)
                            /*} else {
                                firstListElem = false
                            }*/
                        }

                        "<div>" -> {
                            textTopMargin = resources.getDimension(R.dimen.margin_div_top)
                            textBotMargin = resources.getDimension(R.dimen.margin_div_bottom)
                            /*
                            textSize = resources.getDimension(R.dimen.default_text_size)
                            textStyle = Typeface.NORMAL
                            textColor = resources.getColor(R.color.text_default, null)*/
                        }

                        "<h2>" -> {
                            textSize = resources.getDimension(R.dimen.text_size_h2)
                            textStyle = Typeface.BOLD
                            textColor = resources.getColor(R.color.text_header, null)
                            //textTopMargin = resources.getDimension(R.dimen.margin_header_top)
                            //textBotMargin = resources.getDimension(R.dimen.margin_header_bottom)
                        }
                        "<h1>" -> {
                            textSize = resources.getDimension(R.dimen.text_size_h1)
                            textStyle = Typeface.BOLD
                            textColor = resources.getColor(R.color.text_header, null)
                            //textTopMargin = resources.getDimension(R.dimen.margin_default_top)
                            //textBotMargin = resources.getDimension(R.dimen.margin_header_bottom)
                        }

                        "<h3>" -> {
                            textSize = resources.getDimension(R.dimen.text_size_h3)
                            textStyle = Typeface.BOLD
                            textColor = resources.getColor(R.color.text_header, null)
                            //textTopMargin = resources.getDimension(R.dimen.margin_header_top)
                            //textBotMargin = resources.getDimension(R.dimen.margin_header_bottom)
                        }

                        "<h4>" ->  {
                            textSize = resources.getDimension(R.dimen.text_size_h4)
                            textStyle = Typeface.BOLD
                            textColor = resources.getColor(R.color.text_header, null)
                            //textTopMargin = resources.getDimension(R.dimen.margin_header_top)
                            //textBotMargin = resources.getDimension(R.dimen.margin_header_bottom)
                        }

                        "<h5>" ->  {
                            textSize = resources.getDimension(R.dimen.text_size_h5)
                            textStyle = Typeface.BOLD
                            textColor = resources.getColor(R.color.text_header, null)
                            //textTopMargin = resources.getDimension(R.dimen.margin_header_top)
                            //textBotMargin = resources.getDimension(R.dimen.margin_header_bottom)
                        }

                        "<h6>" -> {
                            textSize = resources.getDimension(R.dimen.text_size_h6)
                            textStyle = Typeface.BOLD
                            textColor = resources.getColor(R.color.text_header, null)
                            //textTopMargin = resources.getDimension(R.dimen.margin_header_top)
                            //textBotMargin = resources.getDimension(R.dimen.margin_header_bottom)
                        }

                        "<ol>" -> {
                            orderStack.push(1)
                            ordered = true
                        }

                        "<ul>" -> {
                            ordered = false
                        }

                        "<mark>" -> {
                            mark = true
                            backgroundColor = resources.getColor(R.color.back_mark, null)
                            textColor = resources.getColor(R.color.text_mark, null)
                            textTopMargin = resources.getDimension(R.dimen.margin_default_top)
                        }

                        "<code>" -> {
                            code = true
                            textTypeFace = Typeface.MONOSPACE
                            textTopPadding = resources.getDimension(R.dimen.padding_code_vertical)
                            textBotPadding = resources.getDimension(R.dimen.padding_code_vertical)
                            textTopMargin = resources.getDimension(R.dimen.margin_default_top)
                            if (!mark) {
                                backgroundColor = resources.getColor(R.color.back_code, null)
                                textColor = resources.getColor(R.color.text_code, null)
                            }
                        }
                    }

                    content = text.substring(indexAfterFirst, indexOfSecond)
                    trimmedContent = content.trim()
                    if (listElem) {
                        val marker = StringBuilder()
                        if (ordered) {
                            var order = orderStack.pop()
                            marker.append("$order. ")
                            orderStack.push(++order)
                        } else {
                            marker.append("$unorderedMarker ")
                        }
                        trimmedContent = marker.append(trimmedContent).toString()
                        listElem = false
                    }
                } else {
                    when (first) {
                        "</html>", "</body>" -> return

                        "</h2>", "</h1>", "</h3>", "</h4>", "</h5>", "</h6>" -> {
                            textSize = resources.getDimension(R.dimen.default_text_size)
                            textStyle = Typeface.NORMAL
                            textColor = resources.getColor(R.color.text_default, null)
                            //textTopMargin = resources.getDimension(R.dimen.margin_default_top)
                            //textBotMargin = resources.getDimension(R.dimen.margin_default_bottom)
                        }

                        "</li>" -> {
                            listElem = false
                            textTopMargin = resources.getDimension(R.dimen.margin_default_top)
                            textBotMargin = resources.getDimension(R.dimen.margin_default_bottom)
                        }

                        "</div>" -> {
                            textTopMargin = resources.getDimension(R.dimen.margin_default_top)
                            textBotMargin = resources.getDimension(R.dimen.margin_default_bottom)
                        }

                        "</ol>" -> {
                            orderStack.pop()
                            ordered = false
                            firstListElem = true
                        }

                        "</ul>" -> {
                            firstListElem = true
                        }

                        "</mark>" -> {
                            mark = false
                            if (code) {
                                backgroundColor = resources.getColor(R.color.back_code, null)
                                textColor = resources.getColor(R.color.text_code, null)
                            } else {
                                backgroundColor = resources.getColor(R.color.transparency, null)
                                textColor = resources.getColor(R.color.text_default, null)
                            }
                        }

                        "</code>" -> {
                            code = false
                            textTypeFace = Typeface.DEFAULT
                            textTopPadding = resources.getDimension(R.dimen.padding_default_top)
                            textBotPadding = resources.getDimension(R.dimen.padding_default_bottom)
                            if (!mark) {
                                backgroundColor = resources.getColor(R.color.transparency, null)
                                textColor = resources.getColor(R.color.text_default, null)
                            }
                        }
                    }
                    content = text.substring(indexAfterFirst, indexOfSecond)
                    trimmedContent = content.trim()
                }
                if (trimmedContent.isNotEmpty()) {
                    var viewToAdd = wrap(trimmedContent)
                    (viewToAdd as TextView).textSize = textSize
                    viewToAdd.setTypeface(textTypeFace, textStyle)

                    viewToAdd.setPadding(textLeftPadding.toInt(), textTopPadding.toInt(), textRightPadding.toInt(), textBotPadding.toInt())

                    viewToAdd.setTextColor(textColor)

                    if (code) {
                        val scrollView = HorizontalScrollView(this)
                        val codeLayout = LinearLayout(this)
                        codeLayout.addView(viewToAdd)
                        scrollView.addView(codeLayout)
                        viewToAdd = scrollView
                    }

                    val layoutParams: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.topMargin = textTopMargin.toInt()
                    layoutParams.bottomMargin = textBotMargin.toInt()
                    layoutParams.marginStart = textLeftMargin.toInt()
                    layoutParams.marginEnd = textRightMargin.toInt()

                    viewToAdd.layoutParams = layoutParams

                    viewToAdd.setBackgroundColor(backgroundColor)
                    result.addView(viewToAdd)
                }
                indexAfterFirst = indexOfSecond
            }
        }
    }

    private fun wrap(content: String): View {

        val view = TextView(this)

        val ltText = ltPattern.matcher(content).replaceAll("<")
        val gtText = gtPattern.matcher(ltText).replaceAll(">")
        view.text = nbspPattern.matcher(gtText).replaceAll(" ")

        return view
    }

    private fun clearTabs(text: StringBuilder ): StringBuilder {
        return StringBuilder(Pattern.compile("((?<=[ \t])[ \t]+)")
                .matcher(text)
                .replaceAll(""))
    }

    private fun isOpening(tag: String) = tag.indexOf("/") == -1
}