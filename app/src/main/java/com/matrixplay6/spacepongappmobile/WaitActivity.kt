package com.matrixplay6.spacepongappmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WaitActivity : AppCompatActivity() {

    companion object {
        fun goCountdownActivity(context: Context) {
            val intent = Intent(context, CountdownActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (context is Activity) {
                context.runOnUiThread {
                    context.startActivity(intent)
                }
            } else {
                context.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait)

        WebSocketManager.waitActivity = this
    }
}