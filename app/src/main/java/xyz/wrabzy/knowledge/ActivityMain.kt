package xyz.wrabzy.knowledge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ActivityMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Controller.loadKnowledge()
        val tvLoading = findViewById<TextView>(R.id.tvLoading)
        Thread {
            while (Controller.isNotLoaded()) {
                Thread.sleep(800)
            }
            tvLoading.post {
                val intent = Intent(this, ActivityList::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }.start()
    }
}