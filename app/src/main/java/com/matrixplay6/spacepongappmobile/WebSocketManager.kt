package com.matrixplay6.spacepongappmobile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.json.put
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

object WebSocketManager {
    lateinit var appContext: Context
    public var gameActivity: AppCompatActivity? = null
    public var countdownActivity: CountdownActivity? = null
    public var waitActivity: AppCompatActivity? = null
    public var registerActivity: AppCompatActivity? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private var webSocketClient: WebSocketClient? = null

    // LiveData to notify activities
    private val _messages = MutableLiveData<String>()
    val messages: LiveData<String> get() = _messages

    var username = "KotlinClient"

    fun connect(uri: URI, onConnected: (() -> Unit)? = null) {

        if (webSocketClient != null) return // already connected

        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("WebSocket", "Connected")
                onConnected?.invoke()
            }

            override fun onMessage(message: String?) {
                Log.d("CONNECTION", "Message received: " + message)
                message?.isEmpty()?.let {
                    if (!it) {
                        if (message.trim().startsWith("{") || message.trim().startsWith("[")) {
                            val jsonElement = Json.parseToJsonElement(message)
                            if (jsonElement is JsonObject) {
                                val jsonObject = jsonElement
                                var type = jsonObject["type"]?.jsonPrimitive?.contentOrNull

                                if (type.equals("acceptRegister")) {
                                    Log.d("a", "Server accepted your register. Changing to WaitActivity")
                                    registerActivity?.let { activity ->
                                        RegisterActivity.goWaitActivity(activity)
                                    }
                                }

                                if (type.equals("denyRegister")) {
                                    Log.d("a", "Server denied your register.")
                                    registerActivity?.let { activity ->
                                        RegisterActivity.showRegisterDeniedToast(activity)
                                    }
                                    webSocketClient?.close()
                                    webSocketClient = null
                                }

                                if (type.equals("startCountdown")) {
                                    Log.d("a", "Server started the Countdown. Changing to CountdownActivity")
//                                    waitActivity?.let { activity ->
//                                        WaitActivity.goCountdownActivity(activity)
//                                    }
                                    if (WebSocketManager.waitActivity != null) {
                                        WaitActivity.goCountdownActivity(WebSocketManager.waitActivity!!)
                                    } else if (WebSocketManager.registerActivity != null) {
                                        RegisterActivity.goCountdownActivity(WebSocketManager.registerActivity!!)
                                    }
                                }

                                if (type == "remainingCountdown") {
                                    val value = jsonObject["remainingCountdown"]?.jsonPrimitive?.intOrNull
                                    WebSocketManager.countdownActivity?.let { activity ->
                                        if (value != null) {
                                            CountdownActivity.updateCountdown(activity, value)
                                        }
                                    }
                                }

                                if (type.equals("startGame")) {
                                    Log.d("a", "Server send startGame. Changing to GameActivity")
                                    countdownActivity?.let { activity ->
                                        CountdownActivity.goGameActivity(activity)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("WebSocket", "Closed: $reason")
            }

            override fun onError(ex: Exception?) {
                Log.e("WebSocket", "Error: ${ex?.message}", ex)
            }
        }

        webSocketClient?.connect()

    }

    fun send(type: String, message: String) {
        var jsonObject = buildJsonObject {
            put("type", type)
            put("message", message)
        }
        webSocketClient?.send(Json.encodeToString(jsonObject))
    }

    fun sendRegister(clientName: String) {
        var jsonObject = buildJsonObject {
            put("type", "register")
            put("clientName", clientName)
        }
        webSocketClient?.send(Json.encodeToString(jsonObject))
    }

    fun disconnect() {
        webSocketClient?.close()
        webSocketClient = null
    }
}