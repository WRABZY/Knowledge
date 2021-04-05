package xyz.wrabzy.knowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isVisible

class ActivityList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setBackgroundColor(resources.getColor(R.color.back_default, null))

        val scrollView = ScrollView(this)
        val scrollViewLayout = LinearLayout(this)
        scrollViewLayout.orientation = LinearLayout.VERTICAL
        scrollView.addView(scrollViewLayout)
        rootLayout.addView(scrollView)

        setContentView(rootLayout)


        for (folder in Knowledge.base.folders) {
            if (folder.contentCount > 0) {

                val rowLayout = LinearLayout(this)
                rowLayout.setPadding(resources.getDimension(R.dimen.padding_default_left).toInt(),
                        resources.getDimension(R.dimen.padding_list_activity_vertical).toInt(),
                        0,
                        resources.getDimension(R.dimen.padding_list_activity_vertical).toInt())
                rowLayout.setBackgroundColor(resources.getColor(R.color.back_row, null))
                val rowLayoutParams: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                rowLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_list_activity_row).toInt()
                rowLayout.layoutParams = rowLayoutParams

                val imageArrow = ImageView(this)
                imageArrow.setImageResource(R.drawable.ic_arrow)

                val layoutParams: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layoutParams.gravity = Gravity.CENTER_VERTICAL
                layoutParams.marginEnd = resources.getDimension(R.dimen.margin_list_activity_arrow).toInt()
                imageArrow.layoutParams = layoutParams
                var arrowClosed = true

                val textView = TextView(this)
                textView.text = folder.nameToShow
                textView.textSize = resources.getDimension(R.dimen.text_size_h3)
                val layoutParamsTextView: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                textView.layoutParams = layoutParamsTextView

                rowLayout.addView(imageArrow)
                rowLayout.addView(textView)
                scrollViewLayout.addView(rowLayout)

                val items = ArrayList<TextView>()
                for (innerFolder in folder.folders) {
                    val innerTextView = TextView(this)
                    innerTextView.text = innerFolder.nameToShow
                    innerTextView.setBackgroundColor(resources.getColor(R.color.back_row_deep, null))
                    val innerLayoutParams: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    innerLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_list_activity_row).toInt()
                    innerTextView.layoutParams = innerLayoutParams

                    scrollViewLayout.addView(innerTextView)
                    items.add(innerTextView)
                    innerTextView.isVisible = false
                }

                for (innerArticle in folder.articles) {
                    val innerTextView = TextView(this)
                    innerTextView.text = innerArticle.nameToShow
                    innerTextView.setPadding(resources.getDimension(R.dimen.padding_default_left).toInt(),
                            resources.getDimension(R.dimen.padding_list_activity_vertical).toInt(),
                            0,
                            resources.getDimension(R.dimen.padding_list_activity_vertical).toInt())

                    innerTextView.setBackgroundColor(resources.getColor(R.color.back_row_deep, null))
                    val innerLayoutParams: LinearLayout.LayoutParams  = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    innerLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_list_activity_row).toInt()
                    innerTextView.layoutParams = innerLayoutParams

                    innerTextView.textSize = resources.getDimension(R.dimen.text_size_h5)
                    innerTextView.setTextColor(resources.getColor(R.color.text_header, null))
                    scrollViewLayout.addView(innerTextView)
                    items.add(innerTextView)
                    innerTextView.isVisible = false
                    innerTextView.setOnClickListener {
                        val intent = Intent(this, ActivityArticle::class.java)
                        intent.putExtra("content", innerArticle.content)
                        startActivity(intent)
                    }
                }

                textView.setOnClickListener {
                    for (tv in items) {
                        tv.isVisible = !tv.isVisible
                    }
                    arrowClosed = if (arrowClosed) {
                        imageArrow.setImageResource(R.drawable.ic_arrow_bot)
                        false
                    } else {
                        imageArrow.setImageResource(R.drawable.ic_arrow)
                        true
                    }
                }
            }
        }

        for (article in Knowledge.base.articles) {
            val textView = TextView(this)
            textView.text = article.nameToShow
            textView.setOnClickListener {
                val intent = Intent(this, ActivityArticle::class.java)
                intent.putExtra("content", article.content)
                startActivity(intent)
            }
            scrollViewLayout.addView(textView)
        }


    }
}