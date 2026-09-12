package udb.edu.sv.dsm.agenciaviajes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import udb.edu.sv.dsm.agenciaviajes.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityLoginBinding

    private lateinit var auth:
            FirebaseAuth

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        binding =
            ActivityLoginBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        auth =
            FirebaseAuth.getInstance()

        if (auth.currentUser != null) {

            abrirMain()
        }

        binding.btnLogin
            .setOnClickListener {

                iniciarSesion()
            }

        binding.btnRegister
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        RegisterActivity::class.java
                    )
                )
            }
    }

    private fun iniciarSesion() {

        binding.tvError.visibility =
            View.GONE

        val email =
            binding.etEmail
                .text
                .toString()
                .trim()

        val password =
            binding.etPassword
                .text
                .toString()

        if (
            email.isEmpty() ||
            password.isEmpty()
        ) {

            binding.tvError.text =
                getString(
                    R.string.error_empty_fields
                )

            binding.tvError.visibility =
                View.VISIBLE

            return
        }

        binding.btnLogin.isEnabled =
            false

        auth.signInWithEmailAndPassword(
            email,
            password
        )

            .addOnSuccessListener {

                abrirMain()
            }

            .addOnFailureListener { error ->

                binding.btnLogin.isEnabled =
                    true

                binding.tvError.text =
                    error.localizedMessage
                        ?: "No se pudo iniciar sesión"

                binding.tvError.visibility =
                    View.VISIBLE
            }
    }

    private fun abrirMain() {

        startActivity(
            Intent(
                this,
                MainActivity::class.java
            )
        )

        finish()
    }
}