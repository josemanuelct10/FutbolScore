package com.example.proyectofinal.InicioSesion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyectofinal.databinding.ActivityOlvidarPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class OlvidarPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOlvidarPasswordBinding
     // Obtener instancia de Firebase Auth
     private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOlvidarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Establecer listener del botón de resetear contraseña
        binding.bEnviar.setOnClickListener {

            enviarEmail()
        }

    }

    private fun enviarEmail(){
        // Obtener email del EditText
        val email = binding.etCorreo.text.toString()

        if (email.isNotEmpty()){
            // Enviar correo electrónico de restablecimiento de contraseña
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // El correo electrónico de restablecimiento de contraseña se ha enviado correctamente
                        Toast.makeText(this, "Se ha enviado un correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))

                    } else {
                        // Ha ocurrido un error al enviar el correo electrónico de restablecimiento de contraseña
                        Toast.makeText(this, "Ha ocurrido un error al enviar el correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else {
            Toast.makeText(this, "No se ha insertado ningún correo electrónico.", Toast.LENGTH_SHORT).show()
        }


    }
}