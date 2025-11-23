package com.matrixplay6.spacepongappmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameActivity : AppCompatActivity() {

    private lateinit var sliderPlayer1: SeekBar
    private lateinit var sliderPlayer2: SeekBar
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
        sliderPlayer1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress.toFloat() / maxProgress // 0..1
                Log.d("SliderValue", "Slider 1 value: $value")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Slider derecho (invertido)
        sliderPlayer2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = 1f - progress.toFloat() / maxProgress // invertido: 0 abajo → 1 arriba
                Log.d("SliderValue", "Slider 2 value: $value")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}