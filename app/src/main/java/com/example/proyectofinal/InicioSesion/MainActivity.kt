package com.example.proyectofinal.InicioSesion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyectofinal.Administrador.DrawerLayoutAdmin.DrawerLayoutAdmin
import com.example.proyectofinal.DrawerLayout.DrawerLayout
import com.example.proyectofinal.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    public lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.bInicio.setOnClickListener {
            iniciarSesion()
        }


        binding.bRegistro.setOnClickListener {
            // Cambiamos a la activity de registro de nuevo usuario
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        binding.bOlvidar.setOnClickListener {
            // Cambiamos a la activity de olvidar contraseña
            startActivity(Intent(this, OlvidarPasswordActivity::class.java))
        }

    }

    // Metodo para iniciar sesion
    private fun iniciarSesion(){
        if(binding.ETEmail.text.isNotEmpty() && binding.ETPassword.text.isNotEmpty()){
            // Autetifica que el usuario y contraseña son correctos
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.ETEmail.text.toString(), binding.ETPassword.text.toString()
            ).addOnCompleteListener{
                if(it.isSuccessful){
                    if (binding.ETEmail.text.toString() == "admin@admin.com"){
                        val intent = Intent(this, DrawerLayoutAdmin::class.java)
                        startActivity(intent)
                    }
                    else{
                        val intent = Intent(this, DrawerLayout::class.java)
                        startActivity(intent)
                    }

                    // Si no es correcta se manda un toast
                }else{Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()}
            }

        }else{
            // Toast en caso de que algun campo este vacio
            Toast.makeText(this,"Algún campo está vacío", Toast.LENGTH_SHORT).show()
        }
    }

}