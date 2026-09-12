package udb.edu.sv.dsm.agenciaviajes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.agenciaviajes.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            intentarLogin()
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            irAlCatalogo()
        }
    }

    private fun intentarLogin() {

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError(
                getString(R.string.error_empty_fields)
            )
            return
        }

        mostrarCargando(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                mostrarCargando(false)

                if (task.isSuccessful) {
                    irAlCatalogo()
                } else {
                    mostrarError(
                        getString(R.string.error_login)
                    )
                }
            }
    }

    private fun irAlCatalogo() {

        startActivity(
            Intent(this, MainActivity::class.java)
        )

        finish()
    }

    private fun mostrarError(mensaje: String) {

        binding.tvLoginError.text = mensaje
        binding.tvLoginError.visibility = View.VISIBLE
    }

    private fun mostrarCargando(mostrando: Boolean) {

        binding.progressLogin.visibility =
            if (mostrando) View.VISIBLE else View.GONE

        binding.btnLogin.isEnabled = !mostrando
    }
}