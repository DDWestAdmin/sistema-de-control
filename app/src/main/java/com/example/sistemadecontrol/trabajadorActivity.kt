package com.example.sistemadecontrol

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

data class Trabajador(
    val numeroTarjeta: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val cargo: String = "",
    val fechaIngreso: String = "",
    val fechaSalida: String = ""
)

class trabajadorActivity : AppCompatActivity() {
    companion object {
        val listaTrabajadores = mutableListOf<Trabajador>()
    }

    private var esAdmin = false
    private var Eliminar = false
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trabajador)

        // Inicializar el cliente Firebase Firestore
        db = FirebaseFirestore.getInstance()

        esAdmin = intent.getBooleanExtra("ES_ADMIN", false)
        Eliminar = intent.getBooleanExtra("Eliminar", false)

        val etPanelTrabajador = findViewById<TextView>(R.id.etpaneltrabajador)
        val etNumeroTarjeta = findViewById<EditText>(R.id.etnumerotarjeta)
        val etNombre = findViewById<EditText>(R.id.etnombre)
        val etApellido = findViewById<EditText>(R.id.etapellido)
        val etCargo = findViewById<EditText>(R.id.etcargo)
        val etFechaIngreso = findViewById<EditText>(R.id.etfechaingreso)
        val etFechaSalida = findViewById<EditText>(R.id.etfechasalida)
        val etGuardar = findViewById<Button>(R.id.etguardar)
        val etVaciar = findViewById<Button>(R.id.etvaciar)
        val etVolver = findViewById<Button>(R.id.etvolvertarjetas)

        if (Eliminar) {
            etPanelTrabajador.text = "ELIMINAR TRABAJADOR"
            etGuardar.text = "Eliminar"
            etNombre.visibility = View.GONE
            etApellido.visibility = View.GONE
            etCargo.visibility = View.GONE
            etFechaIngreso.visibility = View.GONE
            etFechaSalida.visibility = View.GONE
        }

        etGuardar.setOnClickListener {
            val numeroTarjeta = etNumeroTarjeta.text.toString().trim()

            if (Eliminar) {
                // DELETE: Elimina la tarjeta en Firebase Firestore
                if (numeroTarjeta.isNotEmpty()) {
                    db.collection("trabajadores").document(numeroTarjeta).delete()
                        .addOnSuccessListener {
                            val trabajadorAEliminar = listaTrabajadores.find { it.numeroTarjeta == numeroTarjeta }
                            if (trabajadorAEliminar != null) {
                                listaTrabajadores.remove(trabajadorAEliminar)
                            }
                            Toast.makeText(this, "Trabajador eliminado correctamente de Firebase", Toast.LENGTH_SHORT).show()
                            etNumeroTarjeta.text.clear()
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(this, "Error al eliminar en Firebase: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Ingrese el número de tarjeta", Toast.LENGTH_SHORT).show()
                }
            } else {
                // CREATE / UPDATE: Guarda el mapa en la colección "trabajadores"
                val nombre = etNombre.text.toString().trim()
                val apellido = etApellido.text.toString().trim()
                val cargo = etCargo.text.toString().trim()
                val fechaIngreso = etFechaIngreso.text.toString().trim()
                val fechaSalida = etFechaSalida.text.toString().trim()

                if (numeroTarjeta.isNotEmpty() && nombre.isNotEmpty()
                    && apellido.isNotEmpty() && cargo.isNotEmpty() && fechaIngreso.isNotEmpty() && fechaSalida.isNotEmpty()) {

                    val persona = hashMapOf(
                        "numeroTarjeta" to numeroTarjeta,
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "cargo" to cargo,
                        "fechaIngreso" to fechaIngreso,
                        "fechaSalida" to fechaSalida
                    )

                    db.collection("trabajadores").document(numeroTarjeta).set(persona)
                        .addOnSuccessListener {
                            val nuevoTrabajador = Trabajador(
                                numeroTarjeta, nombre, apellido, cargo, fechaIngreso, fechaSalida
                            )
                            listaTrabajadores.add(nuevoTrabajador)
                            Toast.makeText(this, "Guardado correctamente en Firebase", Toast.LENGTH_SHORT).show()
                            limpiarCampos(etNumeroTarjeta, etNombre, etApellido, etCargo, etFechaIngreso, etFechaSalida)
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(this, "Error al guardar en Firebase: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        etVaciar.setOnClickListener {
            limpiarCampos(etNumeroTarjeta, etNombre, etApellido, etCargo, etFechaIngreso, etFechaSalida)
        }

        etVolver.setOnClickListener {
            val intent = Intent(this, tarjetasActivity::class.java).apply {
                putExtra("ES_ADMIN", esAdmin)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun limpiarCampos(vararg editTexts: EditText) {
        for (editText in editTexts) {
            editText.text.clear()
        }
    }
}
