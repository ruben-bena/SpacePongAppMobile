package com.matrixplay6.spacepongappmobile

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import androidx.core.widget.doOnTextChanged
import java.net.URI
import android.view.View
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    companion object {
        fun goWaitActivity(context: Context) {
            val intent = Intent(context, WaitActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (context is Activity) {
                context.runOnUiThread {
                    context.startActivity(intent)
                }
            } else {
                context.startActivity(intent)
            }
        }

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

        fun showRegisterDeniedToast(context: Context) {
            if (context is Activity) {
                context.runOnUiThread {
                    Toast.makeText(context, "Name already in use. Try another one please.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private lateinit var nameInput: TextInputEditText
    private lateinit var uriInput: TextInputEditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameInput = findViewById(R.id.nameInput)
        uriInput = findViewById(R.id.uriInput)
        registerButton = findViewById(R.id.registerButton)

        disableButton()

        // Input listeners
        nameInput.doOnTextChanged { text, _, _, _ ->
            decideButtonVisibility()
        }
        uriInput.doOnTextChanged { text, _, _, _ ->
            decideButtonVisibility()
        }

        WebSocketManager.registerActivity = this
    }

    private fun decideButtonVisibility() {
        val isNameFilled = nameInput.text?.isNotBlank() == true
        val isUriFilled = uriInput.text?.isNotBlank() == true
        registerButton.isEnabled = isNameFilled && isUriFilled
    }

    private fun disableButton() {
        registerButton.isEnabled = false
    }

    fun connect(view: View) {
        WebSocketManager.username = nameInput.text.toString()
        try {
            val uri = URI(uriInput.text.toString())
            WebSocketManager.connect(uri) {
                Log.d("CONNECTION", "Register message sent")
                WebSocketManager.sendRegister(nameInput.text.toString())
            }
        } catch (e: Exception) {
            Log.e("CONNECTION", "Invalid URI: ${e.message}")
        }
    }
}