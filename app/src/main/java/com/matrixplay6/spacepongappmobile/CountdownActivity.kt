package com.matrixplay6.spacepongappmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
class CountdownActivity : AppCompatActivity() {

    companion object {
        fun goGameActivity(context: Context) {
            val intent = Intent(context, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (context is Activity) {
                context.runOnUiThread {
                    context.startActivity(intent)
                }
            } else {
                context.startActivity(intent)
            }
        }

        fun updateCountdown(activity: CountdownActivity, value: Int) {
            activity.runOnUiThread {
                val tv = activity.findViewById<TextView>(R.id.countdownNumber)
                tv.text = value.toString()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_countdown)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        WebSocketManager.countdownActivity = this
    }
}