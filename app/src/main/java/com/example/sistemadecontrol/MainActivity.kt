package com.example.sistemadecontrol

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isUnlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etPanelSmartLock = findViewById<TextView>(R.id.etpanelsmartlock)
        val etPanelStatus = findViewById<TextView>(R.id.etpanelstatus)
        val etPuerta = findViewById<Button>(R.id.etpuerta)
        val etLogin = findViewById<Button>(R.id.etlogin)
        val etLogout = findViewById<Button>(R.id.etlogout)
        val etTarjetas = findViewById<Button>(R.id.ettarjetas)
        val esAdmin = intent.getBooleanExtra("ES_ADMIN", false)

        // Boton Puerta
        etPuerta.setOnClickListener {
            if (isUnlocked) {
                isUnlocked = false
                etPanelStatus.text = "ESTADO: CERRADO"
                etPanelStatus.setTextColor(resources.getColor(R.color.red))
                etPanelSmartLock.text = "Desbloquear Puerta"
                etPuerta.text = "Desbloquear"
                Toast.makeText(this, "Puerta Bloqueada", Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Desbloquear Puerta")
                builder.setMessage("Ingrese el número de tarjeta registrado:")

                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton("Desbloquear") { dialog, _ ->
                    val cardNumber = input.text.toString().trim()
                    val registeredCard = trabajadorActivity.listaTrabajadores.find { it.numeroTarjeta == cardNumber }

                    if (registeredCard != null) {
                        isUnlocked = true
                        etPanelStatus.text = "ESTADO: ABIERTO"
                        etPanelStatus.setTextColor(resources.getColor(R.color.green))
                        etPanelSmartLock.text = "Bloquear Puerta"
                        etPuerta.text = "Bloquear"
                        Toast.makeText(this, "Puerta Desbloqueada - Bienvenid@ ${registeredCard.nombre}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Tarjeta no registrada o inválida", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()
            }
        }

        // Boton Iniciar Sesión
        etLogin.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }

        // Boton Acceso Gestor de Tarjetas (Solo Admin)
        etTarjetas.setOnClickListener {
            if (esAdmin) {
                val intent = Intent(this, tarjetasActivity::class.java)
                intent.putExtra("ES_ADMIN", true)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Acceso sólo Administrador", Toast.LENGTH_SHORT).show()
            }
        }

        // Boton Logout
        etLogout.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
