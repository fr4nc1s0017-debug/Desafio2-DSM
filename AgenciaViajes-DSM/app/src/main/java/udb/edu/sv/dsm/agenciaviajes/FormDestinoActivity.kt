package udb.edu.sv.dsm.agenciaviajes

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import udb.edu.sv.dsm.agenciaviajes.databinding.ActivityFormDestinoBinding
import udb.edu.sv.dsm.agenciaviajes.model.Destino
import udb.edu.sv.dsm.agenciaviajes.utils.ImageUtils

class FormDestinoActivity : AppCompatActivity() {

    private lateinit var binding:
            ActivityFormDestinoBinding

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance()

    private val destinosRef =
        database.getReference("destinos")

    private var destinoId: String? = null

    private var imagenBase64Actual =
        ""

    private val pickImageLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri == null) {
                return@registerForActivityResult
            }

            val base64 =
                ImageUtils.uriToBase64(
                    this,
                    uri
                )

            if (base64 == null) {

                mostrarError(
                    getString(
                        R.string.error_image
                    )
                )

                return@registerForActivityResult
            }

            imagenBase64Actual =
                base64

            Glide.with(this)
                .load(uri)
                .into(binding.ivPreview)
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        binding =
            ActivityFormDestinoBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        configurarSpinner()

        destinoId =
            intent.getStringExtra(
                "destinoId"
            )

        if (destinoId == null) {

            binding.tvFormTitle.text =
                getString(
                    R.string.form_title_new
                )

        } else {

            binding.tvFormTitle.text =
                getString(
                    R.string.form_title_edit
                )

            cargarDestino(
                destinoId!!
            )
        }

        binding.btnSelectImage
            .setOnClickListener {

                pickImageLauncher.launch(
                    "image/*"
                )
            }

        binding.btnGuardar
            .setOnClickListener {

                validarYGuardar()
            }
    }

    private fun configurarSpinner() {

        val adapter =
            ArrayAdapter.createFromResource(
                this,
                R.array.countries_array,
                android.R.layout
                    .simple_spinner_dropdown_item
            )

        binding.spinnerPais.adapter =
            adapter
    }

    private fun cargarDestino(
        id: String
    ) {

        destinosRef
            .child(id)
            .get()

            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) {

                    mostrarError(
                        getString(
                            R.string.error_destination_not_found
                        )
                    )

                    return@addOnSuccessListener
                }

                val destino =
                    snapshot.getValue(
                        Destino::class.java
                    )

                if (destino == null) {

                    mostrarError(
                        getString(
                            R.string.error_load_destination
                        )
                    )

                    return@addOnSuccessListener
                }

                binding.etNombre
                    .setText(destino.nombre)

                binding.etPrecio
                    .setText(
                        destino.precio.toString()
                    )

                binding.etDescripcion
                    .setText(
                        destino.descripcion
                    )

                imagenBase64Actual =
                    destino.imagenBase64

                val paises =
                    resources.getStringArray(
                        R.array.countries_array
                    )

                val posicion =
                    paises.indexOf(
                        destino.pais
                    )

                if (posicion >= 0) {

                    binding.spinnerPais
                        .setSelection(
                            posicion
                        )
                }

                val bitmap =
                    ImageUtils.base64ToBitmap(
                        destino.imagenBase64
                    )

                if (bitmap != null) {

                    Glide.with(this)
                        .load(bitmap)
                        .into(
                            binding.ivPreview
                        )
                }
            }

            .addOnFailureListener { error ->

                mostrarError(
                    error.localizedMessage
                        ?: getString(
                            R.string.error_load_destination
                        )
                )
            }
    }

    private fun validarYGuardar() {

        ocultarError()

        val nombre =
            binding.etNombre
                .text
                .toString()
                .trim()

        val precioTexto =
            binding.etPrecio
                .text
                .toString()
                .trim()

        val descripcion =
            binding.etDescripcion
                .text
                .toString()
                .trim()

        val pais =
            binding.spinnerPais
                .selectedItem
                ?.toString()
                ?.trim()
                ?: ""

        if (
            nombre.isEmpty() ||
            precioTexto.isEmpty() ||
            descripcion.isEmpty() ||
            pais.isEmpty()
        ) {

            mostrarError(
                getString(
                    R.string.error_empty_fields
                )
            )

            return
        }

        val precio =
            precioTexto.toDoubleOrNull()

        if (
            precio == null ||
            precio <= 0.0
        ) {

            mostrarError(
                getString(
                    R.string.error_price
                )
            )

            return
        }

        if (
            descripcion.length < 20
        ) {

            mostrarError(
                getString(
                    R.string.error_description_length
                )
            )

            return
        }

        if (
            imagenBase64Actual.isEmpty()
        ) {

            mostrarError(
                getString(
                    R.string.error_image_required
                )
            )

            return
        }

        guardarDestino(
            nombre,
            pais,
            precio,
            descripcion
        )
    }

    private fun guardarDestino(
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String
    ) {

        mostrarCargando(true)

        val usuarioId =
            auth.currentUser?.uid

        if (usuarioId == null) {

            mostrarCargando(false)

            mostrarError(
                getString(
                    R.string.error_not_authenticated
                )
            )

            return
        }

        if (destinoId == null) {

            crearDestino(
                usuarioId,
                nombre,
                pais,
                precio,
                descripcion
            )

        } else {

            actualizarDestino(
                usuarioId,
                nombre,
                pais,
                precio,
                descripcion
            )
        }
    }

    private fun crearDestino(
        usuarioId: String,
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String
    ) {

        val id =
            destinosRef.push().key

        if (id == null) {

            mostrarCargando(false)

            mostrarError(
                getString(
                    R.string.error_generate_id
                )
            )

            return
        }

        val destino =
            Destino(
                id = id,
                nombre = nombre,
                pais = pais,
                precio = precio,
                descripcion = descripcion,
                imagenBase64 =
                    imagenBase64Actual,
                agenteId = usuarioId,
                fechaCreacion =
                    System.currentTimeMillis()
            )

        destinosRef
            .child(id)
            .setValue(destino)

            .addOnSuccessListener {

                mostrarCargando(false)

                finish()
            }

            .addOnFailureListener { error ->

                mostrarCargando(false)

                mostrarError(
                    error.localizedMessage
                        ?: getString(
                            R.string.error_save
                        )
                )
            }
    }

    private fun actualizarDestino(
        usuarioId: String,
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String
    ) {

        val id =
            destinoId ?: return

        val destino =
            Destino(
                id = id,
                nombre = nombre,
                pais = pais,
                precio = precio,
                descripcion = descripcion,
                imagenBase64 =
                    imagenBase64Actual,
                agenteId = usuarioId,
                fechaCreacion = 0L
            )

        destinosRef
            .child(id)
            .get()

            .addOnSuccessListener { snapshot ->

                val destinoAnterior =
                    snapshot.getValue(
                        Destino::class.java
                    )

                destino.fechaCreacion =
                    destinoAnterior
                        ?.fechaCreacion
                        ?: System.currentTimeMillis()

                destinosRef
                    .child(id)
                    .setValue(destino)

                    .addOnSuccessListener {

                        mostrarCargando(false)

                        finish()
                    }

                    .addOnFailureListener { error ->

                        mostrarCargando(false)

                        mostrarError(
                            error.localizedMessage
                                ?: getString(
                                    R.string.error_update
                                )
                        )
                    }
            }

            .addOnFailureListener { error ->

                mostrarCargando(false)

                mostrarError(
                    error.localizedMessage
                        ?: getString(
                            R.string.error_load_destination
                        )
                )
            }
    }

    private fun mostrarError(
        mensaje: String
    ) {

        binding.tvFormError.text =
            mensaje

        binding.tvFormError.visibility =
            View.VISIBLE
    }

    private fun ocultarError() {

        binding.tvFormError.visibility =
            View.GONE

        binding.tvFormError.text =
            ""
    }

    private fun mostrarCargando(
        mostrar: Boolean
    ) {

        binding.progressForm.visibility =
            if (mostrar) {
                View.VISIBLE
            } else {
                View.GONE
            }

        binding.btnGuardar.isEnabled =
            !mostrar

        binding.btnSelectImage.isEnabled =
            !mostrar
    }
}