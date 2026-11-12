package com.example.sistemadecontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class tarjetasActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarjetas)

        // Inicializar cliente Firebase Firestore
        db = FirebaseFirestore.getInstance()

        val esAdmin = intent.getBooleanExtra("ES_ADMIN", false)

        val etHistorial = findViewById<Button>(R.id.ethistorial)
        val etNuevaTarjeta = findViewById<Button>(R.id.etnuevatajeta)
        val etBorrarTarjeta = findViewById<Button>(R.id.etborrartarjeta)
        val etVolver = findViewById<Button>(R.id.etvolver)

        etHistorial.setOnClickListener {
            val intent = Intent(this, historialActivity::class.java).apply {
                putExtra("ES_ADMIN", esAdmin)
            }
            startActivity(intent)
        }

        etNuevaTarjeta.setOnClickListener {
            val intent = Intent(this, trabajadorActivity::class.java).apply {
                putExtra("ES_ADMIN", esAdmin)
                putExtra("Eliminar", false)
            }
            startActivity(intent)
        }

        etBorrarTarjeta.setOnClickListener {
            val intent = Intent(this, trabajadorActivity::class.java).apply {
                putExtra("ES_ADMIN", esAdmin)
                putExtra("Eliminar", true)
            }
            startActivity(intent)
        }

        etVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("ES_ADMIN", esAdmin)
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
