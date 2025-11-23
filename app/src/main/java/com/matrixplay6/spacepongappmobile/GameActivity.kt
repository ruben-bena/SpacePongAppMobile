package com.matrixplay6.spacepongappmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import androidx.compose.ui.graphics.Color

class GameActivity : AppCompatActivity() {

    private lateinit var sliderPlayer1: SeekBar
    private lateinit var sliderPlayer2: SeekBar
    private lateinit var gameCanvas: GameCanvasView
    lateinit var clientName: String
    private val maxProgress = 100000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sliderPlayer1 = findViewById<SeekBar>(R.id.sliderPlayer1)
        sliderPlayer2 = findViewById<SeekBar>(R.id.sliderPlayer2)
        prepareSliders()
        setupSliderListeners()
        disableEnemySlider()

        gameCanvas = findViewById(R.id.gameCanvas)

        WebSocketManager.gameActivity = this
    }

    fun prepareSliders() {
        val screenHeight = resources.displayMetrics.heightPixels

        val params1 = sliderPlayer1.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        params1.width = screenHeight - 200
        params1.marginStart = 5 // Mínimo margen

        val params2 = sliderPlayer2.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        params2.width = screenHeight - 200
        params2.marginEnd = 5 // Mínimo margen

        // Configurar sensibilidad
        sliderPlayer1.max = maxProgress
        sliderPlayer1.progress = maxProgress / 2

        sliderPlayer2.max = maxProgress
        sliderPlayer2.progress = maxProgress / 2

        sliderPlayer1.layoutParams = params1
        sliderPlayer2.layoutParams = params2
    }
    fun setupSliderListeners() {
        // Slider izquierdo (normal)
        if (sliderPlayer1.isEnabled) {
            sliderPlayer1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val value =
                        1f - progress.toFloat() / maxProgress // 0..1
                    //Log.d("SliderValue", "Slider 1 value: $value")
                    WebSocketManager.sendMoveAPP(value)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        // Slider derecho (invertido)
        if (sliderPlayer2.isEnabled) {
            sliderPlayer2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val value =
                        progress.toFloat() / maxProgress // invertido: 0 abajo → 1 arriba
                    //Log.d("SliderValue", "Slider 2 value: $value")
                    WebSocketManager.sendMoveAPP(value)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    fun updateCanvasFromJson(jsonString: String) {
        this.runOnUiThread {
            gameCanvas.updateGameState(jsonString)
        }
    }

    fun disableEnemySlider() {
        WebSocketManager.pendingStartGameJson?.let { json ->
            val json = JSONObject(WebSocketManager.pendingStartGameJson)
            val player1Name = json.getString("player1")

            if (player1Name.equals(WebSocketManager.username)) {
                sliderPlayer2.isEnabled = false
            } else {
                sliderPlayer1.isEnabled = false
            }
            val enabled1 = sliderPlayer1.isEnabled
            val enabled2 = sliderPlayer2.isEnabled
            Log.d("a", "sliderPlayer1.isEnabled=$enabled1, sliderPlayer2.isEnabled=$enabled2")

            WebSocketManager.pendingStartGameJson = null
        }
    }
}