package xyz.wrabzy.knowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import java.io.FileNotFoundException
import java.lang.NullPointerException
import java.net.UnknownHostException

class ActivityLoading : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.isIndeterminate = false
        progressBar.progress = 0
        progressBar.max = 100

        Thread {

            var maybeSeed = InitController.seed()
            var statusChangedOnYellow = false
            var statusChangedOnRed = false

            while (maybeSeed.exceptions.size > 0 && !statusChangedOnRed) {
                if (maybeSeed.exceptions[0].javaClass == UnknownHostException::class.java) {
                    if (!statusChangedOnYellow) {
                        tvStatus.post {
                            tvStatus.text = resources.getString(R.string.unknown_host)
                            tvStatus.setTextColor(resources.getColor(R.color.yellow_status, null))
                            statusChangedOnYellow = true
                            statusChangedOnRed = false
                        }
                    }
                    Thread.sleep(100)
                    maybeSeed = InitController.seed()
                } else  {
                    tvStatus.post {
                        tvStatus.text = resources.getString(R.string.file_not_found)
                        tvStatus.setTextColor(resources.getColor(R.color.red_status, null))
                        statusChangedOnYellow = false
                        statusChangedOnRed = true
                    }
                }
            }

            if (!statusChangedOnRed) {
                if (statusChangedOnYellow) {
                    tvStatus.post {
                        tvStatus.text = ""
                    }
                    statusChangedOnYellow = false
                }

                InitController.growUp(maybeSeed, Knowledge.base)

                do {
                    val progress = InitController.filesLoaded * 100 / maybeSeed.contentCount
                    progressBar.post {
                        progressBar.progress = progress
                    }
                } while (progress != 100)

                tvStatus.post {
                    val intent = Intent(this, ActivityList::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

        }.start()
    }
}