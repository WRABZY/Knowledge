package xyz.wrabzy.knowledge

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding

class ActivityList : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this)
        rootLayout.orientation = LinearLayout.VERTICAL
        rootLayout.setBackgroundColor(resources.getColor(R.color.back_default, null))

        setContentView(rootLayout)

        val addressLayout = LinearLayout(this)
        var address: TextView? = null
        addressLayout.setBackgroundColor(resources.getColor(R.color.white, null))
        rootLayout.addView(addressLayout)

        val urlOfFolder = intent.getStringExtra("url")
        val folderToShow = if (urlOfFolder!!.isEmpty()) {
            val leftStar = TextView(this)
            val lpStar = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            leftStar.text = resources.getString(R.string.app_star)
            leftStar.setTextColor(resources.getColor(R.color.text_banner, null))
            leftStar.setPadding(resources.getDimension(R.dimen.padding_la_banner).toInt(), 0, 0, 0)
            leftStar.layoutParams = lpStar
            val appNameBanner = TextView(this)
            val lpBanner = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            lpBanner.weight = 1F
            appNameBanner.layoutParams = lpBanner
            appNameBanner.text = resources.getString(R.string.app_name_banner)
            appNameBanner.setTextColor(resources.getColor(R.color.back_default, null))
            appNameBanner.setPadding(0, resources.getDimension(R.dimen.padding_la_banner).toInt(), 0, resources.getDimension(R.dimen.padding_la_banner).toInt())
            val rightStar = TextView(this)
            rightStar.text = resources.getString(R.string.app_star)
            rightStar.setTextColor(resources.getColor(R.color.text_banner, null))
            rightStar.setPadding(0, 0, resources.getDimension(R.dimen.padding_la_banner).toInt(), 0)
            rightStar.layoutParams = lpStar
            val bannerArray = arrayOf(leftStar, appNameBanner, rightStar)
            for (view in bannerArray) {
                view.textSize = resources.getDimension(R.dimen.text_la_banner)
                view.setSingleLine()
                view.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                view.gravity = Gravity.CENTER
                addressLayout.addView(view)
            }
            Knowledge.base
        }
        else {
            val homeButton = ImageView(this)
            homeButton.setImageResource(R.drawable.ic_logo)
            homeButton.setColorFilter(resources.getColor(R.color.back_row, null))
            homeButton.setPadding(resources.getDimension(R.dimen.padding_la_status).toInt())
            homeButton.setOnClickListener {
                val intent = Intent(this, ActivityList::class.java)
                intent.putExtra("url", "")
                startActivity(intent)
            }
            addressLayout.addView(homeButton)
            address = TextView(this)
            address.setTextColor(resources.getColor(R.color.back_row, null))
            address.gravity = Gravity.CENTER_VERTICAL
            val lpAddress = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            address.layoutParams = lpAddress
            addressLayout.addView(address)
            Knowledge.base.getFolder(0, ArrayList(urlOfFolder!!.split("/")))
        }

        address?.text = "${intent.getStringExtra("lastUrl")} / ${folderToShow.nameToShow}"

        val rootScrollView = ScrollView(this)
        val scrollViewLayout = LinearLayout(this)
        scrollViewLayout.orientation = LinearLayout.VERTICAL
        rootScrollView.addView(scrollViewLayout)
        rootLayout.addView(rootScrollView)

        for (folder in folderToShow.folders) {

            if (folder.contentCount > 0) {

                val rowLayout = LinearLayout(this)
                rowLayout.setBackgroundColor(resources.getColor(R.color.back_row, null))
                val rowLayoutLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                rowLayoutLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_la_row).toInt()
                rowLayout.layoutParams = rowLayoutLayoutParams

                val folderIcon = ImageView(this)
                folderIcon.setImageResource(R.drawable.baseline_auto_stories_24)
                val folderIconLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                folderIconLayoutParams.marginStart = resources.getDimension(R.dimen.margin_la_folders).toInt()
                folderIconLayoutParams.marginEnd = resources.getDimension(R.dimen.margin_la_folders).toInt()
                folderIcon.layoutParams = folderIconLayoutParams

                val tvFolderName = TextView(this)
                tvFolderName.text = folder.nameToShow
                tvFolderName.textSize = resources.getDimension(R.dimen.text_la_folders)
                val tvFolderNameLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                tvFolderNameLayoutParams.topMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvFolderNameLayoutParams.marginEnd = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvFolderNameLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvFolderNameLayoutParams.weight = 1F
                tvFolderName.layoutParams = tvFolderNameLayoutParams
                tvFolderName.setSingleLine()


                val tvContentCount = TextView(this)
                tvContentCount.hint = String.format("%04d", folder.contentCount)
                val tvContentCountLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                tvContentCountLayoutParams.topMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvContentCountLayoutParams.marginEnd = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvContentCountLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvContentCountLayoutParams.gravity = Gravity.CENTER_VERTICAL
                tvContentCount.layoutParams = tvContentCountLayoutParams

                val tvLastUpdate = TextView(this)
                tvLastUpdate.hint = String.format("%tY.%<tm.%<td", folder.birth)
                val tvLastUpdateLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                tvLastUpdateLayoutParams.topMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvLastUpdateLayoutParams.marginEnd = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvLastUpdateLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
                tvLastUpdateLayoutParams.gravity = Gravity.CENTER_VERTICAL
                tvLastUpdate.layoutParams = tvLastUpdateLayoutParams

                rowLayout.addView(folderIcon)
                rowLayout.addView(tvFolderName)
                rowLayout.addView(tvContentCount)
                rowLayout.addView(tvLastUpdate)

                scrollViewLayout.addView(rowLayout)

                rowLayout.setOnClickListener {
                    val intent = Intent(this, ActivityList::class.java)
                    if (this.intent.getStringExtra("url").isNullOrEmpty()) {
                        intent.putExtra("url", "/${folder.name}")
                        intent.putExtra("lastUrl", "")
                    } else {
                        intent.putExtra("url", "${folder.wayFromRoot}/${folder.name}")
                        intent.putExtra("lastUrl", " / ${folderToShow.nameToShow}")
                    }

                    startActivity(intent)
                }
            }
        }

        for (article in folderToShow.articles) {

            val rowLayout = LinearLayout(this)
            //rowLayout.setBackgroundColor(resources.getColor(R.color.back_row, null))
            val rowLayoutLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            rowLayoutLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_la_row).toInt()
            rowLayout.layoutParams = rowLayoutLayoutParams

            val articleIcon = ImageView(this)
            articleIcon.setImageResource(R.drawable.baseline_article_24)
            val articleIconLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            articleIconLayoutParams.marginStart = resources.getDimension(R.dimen.margin_la_folders).toInt()
            articleIconLayoutParams.marginEnd = resources.getDimension(R.dimen.margin_la_folders).toInt()
            articleIcon.layoutParams = articleIconLayoutParams

            val tvArticleName = TextView(this)
            tvArticleName.text = article.nameToShow
            tvArticleName.textSize = resources.getDimension(R.dimen.text_la_articles)
            val tvArticleNameLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            tvArticleNameLayoutParams.topMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
            tvArticleNameLayoutParams.marginEnd = resources.getDimension(R.dimen.margin_la_folders).toInt()
            tvArticleNameLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
            tvArticleNameLayoutParams.weight = 1F
            tvArticleName.layoutParams = tvArticleNameLayoutParams
            tvArticleName.setSingleLine()

            val tvLastUpdate = TextView(this)
            tvLastUpdate.hint = String.format("%tY.%<tm.%<td", article.birth)
            val tvLastUpdateLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            tvLastUpdateLayoutParams.topMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
            tvLastUpdateLayoutParams.marginEnd = resources.getDimension(R.dimen.margin_la_folders).toInt()
            tvLastUpdateLayoutParams.bottomMargin = resources.getDimension(R.dimen.margin_la_folders).toInt()
            tvLastUpdateLayoutParams.gravity = Gravity.CENTER_VERTICAL
            tvLastUpdate.layoutParams = tvLastUpdateLayoutParams

            rowLayout.addView(articleIcon)
            rowLayout.addView(tvArticleName)
            rowLayout.addView(tvLastUpdate)

            scrollViewLayout.addView(rowLayout)

            rowLayout.setOnClickListener {
                val intent = Intent(this, ActivityArticle::class.java)
                intent.putExtra("content", article.content)
                startActivity(intent)
            }
        }


    }
}