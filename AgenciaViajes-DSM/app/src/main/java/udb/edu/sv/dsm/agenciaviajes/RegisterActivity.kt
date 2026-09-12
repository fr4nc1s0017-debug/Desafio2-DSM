package udb.edu.sv.dsm.agenciaviajes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.udb.agenciaviajes.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            intentarRegistro()
        }

        binding.tvGoLogin.setOnClickListener {
            finish()
        }
    }

    private fun intentarRegistro() {

        val email =
            binding.etRegEmail.text.toString().trim()

        val password =
            binding.etRegPassword.text.toString().trim()

        val confirm =
            binding.etRegConfirmPassword.text.toString().trim()

        if (
            email.isEmpty() ||
            password.isEmpty() ||
            confirm.isEmpty()
        ) {
            mostrarError(
                getString(R.string.error_empty_fields)
            )
            return
        }

        if (password != confirm) {
            mostrarError(
                getString(R.string.error_password_match)
            )
            return
        }

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {
                finish()
            } else {
                mostrarError(
                    task.exception?.localizedMessage
                        ?: getString(R.string.error_login)
                )
            }
        }
    }

    private fun mostrarError(mensaje: String) {

        binding.tvRegisterError.text = mensaje
        binding.tvRegisterError.visibility = View.VISIBLE
    }
}