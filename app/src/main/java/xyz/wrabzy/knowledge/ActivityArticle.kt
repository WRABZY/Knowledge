package xyz.wrabzy.knowledge

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import java.lang.StringBuilder

import java.util.regex.Pattern
import java.util.Deque
import java.util.ArrayDeque

class ActivityArticle : AppCompatActivity() {

    private val ltPattern = Pattern.compile("&lt;")
    private val gtPattern = Pattern.compile("&gt;")
    private val nbspPattern = Pattern.compile("&nbsp;")
    private val tagPattern = Pattern.compile("(<\\p{L}+\\d?>|</\\p{L}+\\d?>)")
    private val tabPattern = Pattern.compile("((?<=[ \t])[ \t]+)")
    private val emptyPattern = Pattern.compile("^[ \n\t\r]*$")
    private val orderStack: Deque<Int> = ArrayDeque()
    private val unorderedMarker by lazy { resources.getString(R.string.unorderedMarker) }

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
        scrollViewLayout.setPadding(resources.getDimension(R.dimen.padding_aa_layout).toInt())

        var articleText = StringBuilder()
        articleText.append(this.intent.getStringExtra("content"))
        articleText = clearTabs(articleText)

        buildActivity(articleText, scrollViewLayout)
    }

    private fun buildActivity(text: StringBuilder, result: LinearLayout) {

        val tagMatcher = tagPattern.matcher(text)

        var firstTag: String
        var indexAfterFirst = 0
        var secondTag: String
        var indexOfSecond: Int

        var content: String

        var ordered = false

        var codeView: TextView? = null

        var viewToAdd: View? = null

        while (tagMatcher.find(indexAfterFirst)) {

            firstTag = tagMatcher.group()
            indexAfterFirst = text.indexOf(firstTag, indexAfterFirst) + firstTag.length

            if (tagMatcher.find(indexAfterFirst)) {
                secondTag = tagMatcher.group()
                indexOfSecond = text.indexOf(secondTag, indexAfterFirst)
                content = convertSpecials(text.substring(indexAfterFirst, indexOfSecond))

                when(firstTag) {
                    "<h1>" -> viewToAdd = if (secondTag == "</h1>") {
                                              wrapH1(content)
                                          } else {
                                              null
                                          }

                    "<h2>" -> viewToAdd = if (secondTag == "</h2>") {
                                              wrapH2(content)
                                          } else {
                                              null
                                          }

                    "<h3>" -> viewToAdd = if (secondTag == "</h3>") {
                                              wrapH3(content)
                                          } else {
                                              null
                                          }

                    "<h4>" -> viewToAdd = if (secondTag == "</h4>") {
                                              wrapH4(content)
                                          } else {
                                              null
                                          }

                    "<h5>" -> viewToAdd = if (secondTag == "</h5>") {
                                              wrapH5(content)
                                          } else {
                                              null
                                          }

                    "<h6>" -> viewToAdd = if (secondTag == "</h6>") {
                                              wrapH6(content)
                                          } else {
                                              null
                                          }

                    "<p>" -> viewToAdd = if (secondTag == "</p>") {
                                             wrapP(content)
                                         } else {
                                             wrapPBegin(content)
                                         }

                    "<div>" -> viewToAdd = if (secondTag == "</div>") {
                                               wrapD(content)
                                           } else {
                                               wrapDBegin(content)
                                           }

                    "<code>" ->  viewToAdd = if (secondTag == "</code>") {
                                                 if (codeView == null) {
                                                     codeView = wrapC(content)
                                                     val scrollView = HorizontalScrollView(this)
                                                     val llScrollLayout = LinearLayout(this)
                                                     llScrollLayout.addView(codeView)
                                                     scrollView.addView(llScrollLayout)
                                                     scrollView.setBackgroundColor(resources.getColor(R.color.black, null))
                                                     scrollView
                                                 } else {
                                                     wrapCAdd(codeView, content)
                                                     null
                                                 }
                                             } else {
                                                 null
                                             }

                    "<mark>" -> viewToAdd = if (secondTag == "</mark>") {
                                                wrapM(content)
                                            } else {
                                                null
                                            }

                    "<li>" -> viewToAdd = //if (secondTag == "</li>") {
                                              if (ordered) wrapOL(content)
                                              else wrapUL(content)
                                          /*} else {
                                              if (ordered) wrapOLBegin(content)
                                              else wrapULBegin(content)
                                          }*/

                    "<ol>" -> {
                        ordered = true
                        orderStack.push(1)
                        viewToAdd = wrap(content)
                    }

                    "<ul>" -> {
                        ordered = false
                        viewToAdd = wrap(content)
                    }

                    "</h1>", "</h2>", "</h3>",
                    "</h4>", "</h5>", "</h6>",
                    "</div>", "</p>", "</ul>",
                    "</mark>" -> viewToAdd = if (secondTag == "</p>") {
                                                 wrapPEnd(content)
                                             } else if (secondTag == "</div>") {
                                                 wrapDEnd(content)
                                             } else {
                                                 wrap(content)
                                             }

                    "</code>" -> viewToAdd = if (secondTag == "</p>") {
                                                 codeView = null
                                                 wrapPEnd(content)
                                             } else if (secondTag == "</div>") {
                                                 codeView = null
                                                 wrapDEnd(content)
                                             } else if (secondTag == "<code>" && emptyPattern.matcher(content).find()) {
                                                 null
                                             } else {
                                                 codeView = null
                                                 wrap(content)
                                             }

                    "</ol>" -> {
                        viewToAdd = if (secondTag == "</p>") {
                                        wrapPEnd(content)
                                    } else if (secondTag == "</div>") {
                                        wrapDEnd(content)
                                    } else {
                                        wrap(content)
                                    }
                        orderStack.pop()
                    }

                    else -> viewToAdd = null
                }

            }

            if (viewToAdd != null) {
                result.addView(viewToAdd)
            }
        }
    }

    private fun wrap(content: String): TextView {
        val view = TextView(this)
        view.typeface = Typeface.SERIF
        if (emptyPattern.matcher(content).find()) {
            view.height = 0
        }
        view.text = content
        return view
    }

    private fun wrapH1(content: String): TextView {
        val view = wrap(content)
        view.textSize = resources.getDimension(R.dimen.text_aa_h1)
        view.gravity = Gravity.CENTER_HORIZONTAL
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.topMargin = resources.getDimension(R.dimen.margin_aa_h1).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapH2(content: String): TextView {
        val view = wrap(content)
        view.textSize = resources.getDimension(R.dimen.text_aa_h2)
        return view
    }

    private fun wrapH3(content: String): TextView {
        val view = wrap(content)
        view.textSize = resources.getDimension(R.dimen.text_aa_h3)
        return view
    }

    private fun wrapH4(content: String): TextView {
        val view = wrap(content)
        view.textSize = resources.getDimension(R.dimen.text_aa_h4)
        return view
    }

    private fun wrapH5(content: String): TextView {
        val view = wrap(content)
        view.textSize = resources.getDimension(R.dimen.text_aa_h5)
        return view
    }

    private fun wrapH6(content: String): TextView {
        val view = wrap(content)
        view.textSize = resources.getDimension(R.dimen.text_aa_h6)
        return view
    }

    private fun wrapP(content: String): TextView {
        val view = wrap(content)
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.topMargin = resources.getDimension(R.dimen.margin_aa_p).toInt()
        lpView.bottomMargin = resources.getDimension(R.dimen.margin_aa_p).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapPBegin(content: String): TextView {
        val view = wrap(content)
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.topMargin = resources.getDimension(R.dimen.margin_aa_p).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapPEnd(content: String): TextView {
        val view = wrap(content)
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.bottomMargin = resources.getDimension(R.dimen.margin_aa_p).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapD(content: String): TextView {
        val view = wrap(content)
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.topMargin = resources.getDimension(R.dimen.margin_aa_div).toInt()
        lpView.bottomMargin = resources.getDimension(R.dimen.margin_aa_div).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapDBegin(content: String): TextView {
        val view = wrap(content)
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.topMargin = resources.getDimension(R.dimen.margin_aa_div).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapDEnd(content: String): TextView {
        val view = wrap(content)
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.bottomMargin = resources.getDimension(R.dimen.margin_aa_div).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapC(content: String): TextView {
        val view = wrap(content)
        view.typeface = Typeface.MONOSPACE
        view.setTextColor(resources.getColor(R.color.code_text, null))
        view.setPadding(resources.getDimension(R.dimen.padding_aa_code).toInt(), resources.getDimension(R.dimen.padding_aa_code).toInt(), 0, resources.getDimension(R.dimen.padding_aa_code).toInt())
        return view
    }

    private fun wrapCAdd(codeView: TextView, content: String): Unit {
        codeView.append("\n$content")
    }

    private fun wrapM(content: String): TextView {
        val view = wrap(content)
        view.typeface = Typeface.create(view.typeface, Typeface.BOLD)
        view.setTextColor(resources.getColor(R.color.mark_text, null))
        view.setBackgroundColor(resources.getColor(R.color.mark_back, null))
        view.setPadding(resources.getDimension(R.dimen.padding_aa_mark).toInt())
        return view
    }

    private fun wrapOL(content: String): TextView {
        val listedContent = "${orderStack.peek()}. $content"
        orderStack.push(orderStack.pop() + 1)
        val view = wrap(listedContent)
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.topMargin = resources.getDimension(R.dimen.margin_aa_li).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun wrapUL(content: String): TextView {
        val view = wrap("$unorderedMarker $content")
        val lpView = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lpView.topMargin = resources.getDimension(R.dimen.margin_aa_li).toInt()
        view.layoutParams = lpView
        return view
    }

    private fun convertSpecials(content: String): String {
        val ltText = ltPattern.matcher(content.trim()).replaceAll("<")
        val gtText = gtPattern.matcher(ltText).replaceAll(">")
        return nbspPattern.matcher(gtText).replaceAll(" ")
    }

    private fun clearTabs(text: StringBuilder) = StringBuilder(tabPattern.matcher(text).replaceAll(""))
}