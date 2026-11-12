package com.example.sistemadecontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class historialActivity : AppCompatActivity() {
    private var esAdmin = false
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        esAdmin = intent.getBooleanExtra("ES_ADMIN", false)

        val etHistorial = findViewById<TextView>(R.id.ethistorial)
        val etVolver = findViewById<Button>(R.id.etvolvertarjetas)

        // Inicializar cliente Firebase Firestore
        db = FirebaseFirestore.getInstance()

        etHistorial.text = "Consultando Firebase..."

        // Solicitar a Firestore los registros de la colección "trabajadores"
        db.collection("trabajadores").get(Source.SERVER)
            .addOnSuccessListener { documentos ->
                val historial = StringBuilder()

                for (documento in documentos) {
                    val numeroTarjeta = documento.getString("numeroTarjeta") ?: documento.id
                    val nombre = documento.getString("nombre") ?: ""
                    val apellido = documento.getString("apellido") ?: ""
                    val cargo = documento.getString("cargo") ?: ""
                    val fechaIngreso = documento.getString("fechaIngreso") ?: ""
                    val fechaSalida = documento.getString("fechaSalida") ?: ""

                    historial.append("--------------------------------------------------------\n\n")
                    historial.append("Número de Tarjeta: $numeroTarjeta\n")
                    historial.append("Nombre: $nombre || Apellido: $apellido\n")
                    historial.append("Cargo: $cargo\n")
                    historial.append("Fecha de Ingreso: $fechaIngreso\n")
                    historial.append("Fecha de Salida: $fechaSalida\n\n")
                    historial.append("--------------------------------------------------------\n\n")
                }

                etHistorial.text = if (historial.isEmpty()) {
                    "No existen registros en Firebase"
                } else {
                    historial.toString()
                }
            }
            .addOnFailureListener { error ->
                etHistorial.text = "Error al recuperar datos: ${error.message}"
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }

        etVolver.setOnClickListener {
            val intent = Intent(this, tarjetasActivity::class.java).apply {
                putExtra("ES_ADMIN", esAdmin)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
