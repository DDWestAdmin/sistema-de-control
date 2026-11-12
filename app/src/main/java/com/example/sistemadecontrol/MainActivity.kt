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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class MainActivity : AppCompatActivity() {
    private var isUnlocked = false
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el cliente Firebase Firestore
        db = FirebaseFirestore.getInstance()

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
                etPanelStatus.setTextColor(resources.getColor(R.color.red, theme))
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

                    if (cardNumber.isEmpty()) {
                        Toast.makeText(this, "Debe ingresar un número de tarjeta", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Consultar en Firebase Firestore si existe el número de tarjeta en "trabajadores"
                    db.collection("trabajadores").document(cardNumber).get(Source.SERVER)
                        .addOnSuccessListener { documento ->
                            if (documento.exists()) {
                                val nombre = documento.getString("nombre") ?: "Trabajador"
                                isUnlocked = true
                                etPanelStatus.text = "ESTADO: ABIERTO"
                                etPanelStatus.setTextColor(resources.getColor(R.color.green, theme))
                                etPanelSmartLock.text = "Bloquear Puerta"
                                etPuerta.text = "Bloquear"
                                Toast.makeText(this, "Puerta Desbloqueada - Bienvenid@ $nombre", Toast.LENGTH_SHORT).show()
                            } else {
                                // Buscar alternativamente en la lista en memoria como fallback
                                val registeredCard = trabajadorActivity.listaTrabajadores.find { it.numeroTarjeta == cardNumber }
                                if (registeredCard != null) {
                                    isUnlocked = true
                                    etPanelStatus.text = "ESTADO: ABIERTO"
                                    etPanelStatus.setTextColor(resources.getColor(R.color.green, theme))
                                    etPanelSmartLock.text = "Bloquear Puerta"
                                    etPuerta.text = "Bloquear"
                                    Toast.makeText(this, "Puerta Desbloqueada - Bienvenid@ ${registeredCard.nombre}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Tarjeta no registrada o inválida en Firebase", Toast.LENGTH_SHORT).show()
                                }
                            }
                            dialog.dismiss()
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(this, "Error al consultar Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
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
                val intent = Intent(this, tarjetasActivity::class.java).apply {
                    putExtra("ES_ADMIN", true)
                }
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
