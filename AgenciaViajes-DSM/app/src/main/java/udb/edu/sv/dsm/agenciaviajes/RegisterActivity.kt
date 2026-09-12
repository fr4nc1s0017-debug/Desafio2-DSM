package udb.edu.sv.dsm.agenciaviajes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import udb.edu.sv.dsm.agenciaviajes.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityRegisterBinding

    private lateinit var auth:
            FirebaseAuth

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        binding =
            ActivityRegisterBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        auth =
            FirebaseAuth.getInstance()

        binding.btnCreateAccount
            .setOnClickListener {

                registrarUsuario()
            }
    }

    private fun registrarUsuario() {

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

        val confirmPassword =
            binding.etConfirmPassword
                .text
                .toString()

        if (
            email.isEmpty() ||
            password.isEmpty() ||
            confirmPassword.isEmpty()
        ) {

            mostrarError(
                getString(
                    R.string.error_empty_fields
                )
            )

            return
        }

        if (
            password != confirmPassword
        ) {

            mostrarError(
                "Las contraseñas no coinciden"
            )

            return
        }

        if (
            password.length < 6
        ) {

            mostrarError(
                "La contraseña debe tener al menos 6 caracteres"
            )

            return
        }

        binding.btnCreateAccount
            .isEnabled = false

        auth.createUserWithEmailAndPassword(
            email,
            password
        )

            .addOnSuccessListener {

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )

                finish()
            }

            .addOnFailureListener { error ->

                binding.btnCreateAccount
                    .isEnabled = true

                mostrarError(
                    error.localizedMessage
                        ?: "No se pudo crear la cuenta"
                )
            }
    }

    private fun mostrarError(
        mensaje: String
    ) {

        binding.tvError.text =
            mensaje

        binding.tvError.visibility =
            View.VISIBLE
    }
}