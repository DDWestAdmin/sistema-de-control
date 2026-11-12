package com.example.sistemadecontrol

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sistemadecontrol.MainActivity

class loginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etCorreo = findViewById<EditText>(R.id.etcorreo)
        val etContrasenna = findViewById<EditText>(R.id.etcontrasenna)
        val etBoton = findViewById<Button>(R.id.etbutton)
        val etBorrador = findViewById<Button>(R.id.etborrador)
        val etInicio = findViewById<Button>(R.id.etinicio)

        etBoton.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasenna = etContrasenna.text.toString().trim()

            //Validación de campos vacíos
            if (correo.isEmpty() || contrasenna.isEmpty()) {
                Toast.makeText(this, "Necesita completar los campos vacíos", Toast.LENGTH_SHORT).show()
            }else {
                //calidación credenciales administrador
                val esAdmin = (correo == "Administrador" && contrasenna == "Admin1234")
                if (esAdmin) {
                    Toast.makeText(this, "Credenciales correctas Administrador", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("ES_ADMIN", true)
                    }
                    startActivity(intent)
                    finish()
                } else{
                    //mensaje campos erróneos
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
            }
        //Boton Borrador
        etBorrador.setOnClickListener {
            etCorreo.text.clear()
            etContrasenna.text.clear()
        }
        //Boton Inicio
        etInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
