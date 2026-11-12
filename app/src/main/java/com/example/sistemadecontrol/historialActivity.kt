package com.example.sistemadecontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemadecontrol.trabajadorActivity.Companion.listaTrabajadores

class historialActivity : AppCompatActivity() {
    private var esAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        esAdmin = intent.getBooleanExtra("ES_ADMIN", false)

        val etHistorial = findViewById<TextView>(R.id.ethistorial)
        val etVolver = findViewById<Button>(R.id.etvolvertarjetas)

        val lista = listaTrabajadores

        if (lista.isEmpty()) {
            etHistorial.text = "No hay Trabajadores registrados"
        } else {
            val historial = StringBuilder()
            for (t in lista) {
                historial.append("--------------------------------------------------------\n\n")
                historial.append("Número de Tarjeta: ${t.numeroTarjeta}\n")
                historial.append("Nombre: ${t.nombre} || Apellido: ${t.apellido}\n")
                historial.append("Cargo: ${t.cargo}\n")
                historial.append("Fecha de Ingreso: ${t.fechaIngreso}\n")
                historial.append("Fecha de Salida: ${t.fechaSalida}\n\n")
                historial.append("--------------------------------------------------------\n\n")
            }
            etHistorial.text = historial.toString()
        }

        etVolver.setOnClickListener {
            val intent = Intent(this, tarjetasActivity::class.java)
            intent.putExtra("ES_ADMIN", esAdmin)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
