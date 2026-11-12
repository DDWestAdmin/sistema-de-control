package com.example.sistemadecontrol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

class loginActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etCorreo = findViewById<EditText>(R.id.etcorreo)
        val etContrasenna = findViewById<EditText>(R.id.etcontrasenna)
        val etBoton = findViewById<Button>(R.id.etbutton)
        val etBorrador = findViewById<Button>(R.id.etborrador)
        val etInicio = findViewById<Button>(R.id.etinicio)

        // Inicializar el cliente de Firebase Firestore
        db = FirebaseFirestore.getInstance()

        etBoton.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasenna = etContrasenna.text.toString().trim()

            // Validación de campos vacíos
            if (correo.isEmpty() || contrasenna.isEmpty()) {
                Toast.makeText(this, "Necesita completar los campos vacíos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Consulta a Firestore en la colección "usuarios"
            db.collection("usuarios")
                .whereEqualTo("correo", correo)
                .whereEqualTo("contrasenna", contrasenna)
                .get(Source.SERVER)
                .addOnSuccessListener { documentos ->
                    if (!documentos.isEmpty) {
                        val doc = documentos.documents[0]
                        val esAdmin = doc.getBoolean("esAdmin")
                            ?: (doc.getString("rol") == "admin" || correo.equals("Administrador", ignoreCase = true))

                        Toast.makeText(this, "Credenciales correctas", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("ES_ADMIN", esAdmin)
                        }
                        startActivity(intent)
                        finish()
                    } else if (correo == "Administrador" && contrasenna == "Admin1234") {
                        // Credenciales por defecto para Administrador
                        Toast.makeText(this, "Credenciales correctas Administrador", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("ES_ADMIN", true)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { error ->
                    // Fallback a credenciales por defecto si no hay conexión a servidor
                    if (correo == "Administrador" && contrasenna == "Admin1234") {
                        Toast.makeText(this, "Credenciales correctas Administrador", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("ES_ADMIN", true)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Botón Vaciar Campos
        etBorrador.setOnClickListener {
            etCorreo.text.clear()
            etContrasenna.text.clear()
        }

        // Botón Volver a Inicio
        etInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
