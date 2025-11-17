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
                                    RegisterActivity.goWaitActivity(waitActivity as Activity)
                                }
                                /*
                                // Aquí se harán cosas dependiendo del tipo de mensaje que llegue
                                if (type.equals("clients")) {
                                    // Aquí mostrar todos los jugadores y hacer que pueda mandar la solicitud
                                    var lista = jsonObject["list"]?.jsonArray
                                    var str = ""
                                    if (lista != null) {
                                        for (item in lista) {
                                            str += item
                                            if (lista.indexOf(item) != lista.size) {
                                                str += ","
                                            }
                                            Log.d("ITEMWSM", item.toString())
                                        }
                                    }
                                    ViewClients.clients = str
                                    ViewClients.updateLayout(
                                        viewClientsActivity as Activity,
                                        ViewClients.clients
                                    )

                                }
                                if (type.equals("challenge")) {
                                    var challenger =
                                        jsonObject["challenger"]?.jsonPrimitive?.contentOrNull
                                    Log.d("CONNECTION", "Challenger: " + challenger)
                                    // Aceptando el challenge
                                    println("Entro en sendStartMatch")


                                    //val matchJson = buildJsonObject {
                                    //    put("type", "startMatch")
                                    //    put("player_1", challenger)
                                    //    put("player_2", username)
                                    //}
                                    //send(Json.encodeToString(matchJson))
                                    ViewClients.showChallenge(
                                        viewClientsActivity as Activity,
                                        challenger.toString()
                                    )
                                }

                                if (type.equals("confirmedGame")) {
                                    // Thread.sleep(3000)
                                    // RegisterActivity.goGameActivity(appContext)
                                }

                                if (type.equals("startMatch")) {
                                    Log.d("STARTMATCH", "ENTRO EN STARTMATCH")

                                }

                                if (type.equals("remainingCountdown")) {
                                    var timeRemaining =
                                        jsonObject["value"]?.jsonPrimitive?.contentOrNull
                                    CountdownActivity.countdownTime = timeRemaining?.toInt()!!
                                    CountdownActivity.updateTime(countdownActivity as Activity)

                                    if(timeRemaining.toInt() == 0) {
                                        CountdownActivity.goGameActivity(countdownActivity as Activity)
                                    }
                                }

                                if (type.equals("startCountdown")) {
                                    ViewClients.goCountdownActivity(viewClientsActivity as Activity)
                                    var player1 =
                                        jsonObject["player_1"]?.jsonPrimitive?.contentOrNull.toString()
                                    var player2 =
                                        jsonObject["player_2"]?.jsonPrimitive?.contentOrNull.toString()
                                    CountdownActivity.name1 = player1
                                    CountdownActivity.name2 = player2
                                }

                                if (type.equals("confirmedRegister")) {
                                    RegisterActivity.goViewClients(registerActivity as Activity)
                                }

                                if (type.equals("clientNameNotAvalible")) {
                                    RegisterActivity.showNameNotAvlb(registerActivity as Activity)
                                }

                                if (type.equals("drawOrder")) {
                                    // Log.d("CONNECTION", "drawOrder")

                                    var gridStr = jsonObject["grid"]?.jsonArray
                                    gridStr.let { array ->
                                        if (array != null) {
                                            // Actualizando la grid
                                            var resultGrid = mutableListOf<String>()
                                            for (element in array) {
                                                // Log.d("GRID", GameActivity.grid.toString())
                                                resultGrid.add(element.toString())

                                            }
                                            GameActivity.grid = resultGrid

                                            GameActivity.updateGrid()


                                            // Log.d("CURRENT_PLAYER", GameActivity.curPlayer.toString())
                                        }

                                    }
                                    // Cambiando el turno al correcto
                                    var current_turn =
                                        jsonElement["turn"]?.jsonPrimitive?.contentOrNull.toString()
                                    GameActivity.curPlayer = current_turn.toInt()
                                    Log.d("TURN", GameActivity.curPlayer.toString())

                                    // Log.d("CONNECTION", gridStr.toString())
                                    var winner = jsonElement["winner"]
                                    GameActivity.winner = winner.toString()
                                    if (winner.toString().replace("\"", "") != "none") {
                                        GameActivity.showGameResult(gameActivity as Activity)
                                    }

                                    // Log.d("WINNER", GameActivity.winner)

                                }

                                if (type.equals("gameOutcome")) {
                                    GameActivity.winner = jsonElement["winnerName"].toString()
                                    GameActivity.showGameResult(gameActivity as Activity)
                                }


                                 */

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