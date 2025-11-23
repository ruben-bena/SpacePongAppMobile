package com.matrixplay6.spacepongappmobile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import org.json.JSONObject

class GameCanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val bgPaint = Paint().apply {
        color = Color.BLACK
    }

    private val objectPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 100f // Tamaño base, lo ajustaremos dinámicamente si quieres
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private val midLinePaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 5f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 20f), 0f)
        style = Paint.Style.STROKE
    }

    private var ballX = 0.5f
    private var ballY = 0.5f
    private var p1Y = 0.5f
    private var p2Y = 0.5f
    private var scoreP1 = 0
    private var scoreP2 = 0

    private val ballRadiusRatio = 0.02f
    private val paddleWidthRatio = 0.03f
    private val paddleHeightRatio = 0.15f
    private val paddleMarginX = 0.05f

    fun updateGameState(jsonString: String) {
        try {
            val json = JSONObject(jsonString)

            // Extract data
            val ball = json.getJSONObject("ball")
            val paddles = json.getJSONObject("paddles")
            val p1 = paddles.getJSONObject("p1")
            val p2 = paddles.getJSONObject("p2")
            val score = json.getJSONObject("score")

            // Upadte local variables
            ballX = ball.getDouble("x").toFloat()
            ballY = ball.getDouble("y").toFloat()

            p1Y = p1.getDouble("y").toFloat()
            p2Y = p2.getDouble("y").toFloat()

            scoreP1 = score.getInt("p1")
            scoreP2 = score.getInt("p2")

            // Drawing
            invalidate()

        } catch (e: Exception) {
            e.printStackTrace() // Si el JSON viene mal, no crasheamos, solo lo ignoramos
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // 1. Fondo
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // 2. Línea central (opcional, para estética Pong)
        canvas.drawLine(w / 2, 0f, w / 2, h, midLinePaint)

        // 3. Marcador
        // Dibujamos el texto centrado en la parte superior (al 10% de la altura)
        val scoreText = "$scoreP1   $scoreP2"
        canvas.drawText(scoreText, w / 2, h * 0.15f, textPaint)

        // 4. Palas (Jugadores)
        // Calculamos dimensiones en píxeles
        val pW = w * paddleWidthRatio
        val pH = h * paddleHeightRatio

        // Jugador 1 (Izquierda)
        // X fija al margen izquierdo. Y centrada según el JSON.
        val p1XPos = w * paddleMarginX
        // Restamos pH/2 porque la coordenada del JSON suele ser el CENTRO de la pala
        val p1Top = (p1Y * h) - (pH / 2)
        canvas.drawRect(p1XPos, p1Top, p1XPos + pW, p1Top + pH, objectPaint)

        // Jugador 2 (Derecha)
        val p2XPos = w * (1f - paddleMarginX) - pW
        val p2Top = (p2Y * h) - (pH / 2)
        canvas.drawRect(p2XPos, p2Top, p2XPos + pW, p2Top + pH, objectPaint)

        // 5. Bola
        val bRadius = w * ballRadiusRatio
        val bX = ballX * w
        val bY = ballY * h
        canvas.drawCircle(bX, bY, bRadius, objectPaint)
    }
}