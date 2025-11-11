package com.matrixplay6.spacepongappmobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import androidx.core.widget.doOnTextChanged

class RegisterActivity : AppCompatActivity() {

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
    }

    private fun decideButtonVisibility() {
        val isNameFilled = nameInput.text?.isNotBlank() == true
        val isUriFilled = uriInput.text?.isNotBlank() == true
        registerButton.isEnabled = isNameFilled && isUriFilled
    }

    private fun disableButton() {
        registerButton.isEnabled = false
    }
}