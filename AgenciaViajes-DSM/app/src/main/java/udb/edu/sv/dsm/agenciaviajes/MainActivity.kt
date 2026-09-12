package udb.edu.sv.dsm.agenciaviajes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import udb.edu.sv.dsm.agenciaviajes.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import udb.edu.sv.dsm.agenciaviajes.adapter.DestinoAdapter
import udb.edu.sv.dsm.agenciaviajes.model.Destino
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance()

    private val destinosRef =
        database.getReference("destinos")

    private lateinit var adapter: DestinoAdapter

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityMainBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        setSupportActionBar(
            binding.toolbar
        )

        configurarRecyclerView()

        binding.fabAdd.setOnClickListener {

            abrirFormulario(null)
        }

        escucharDestinos()
    }

    private fun configurarRecyclerView() {

        adapter =
            DestinoAdapter(
                emptyList(),

                onItemClick = { destino ->

                    abrirFormulario(
                        destino.id
                    )
                },

                onDeleteClick = { destino ->

                    confirmarEliminar(
                        destino
                    )
                }
            )

        binding.rvDestinos.layoutManager =
            LinearLayoutManager(this)

        binding.rvDestinos.adapter =
            adapter
    }

    private fun escucharDestinos() {

        destinosRef.addValueEventListener(

            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    val lista =
                        mutableListOf<Destino>()

                    for (
                    destinoSnapshot
                    in snapshot.children
                    ) {

                        val destino =
                            destinoSnapshot
                                .getValue(
                                    Destino::class.java
                                )

                        if (destino != null) {

                            if (destino.id.isEmpty()) {

                                destino.id =
                                    destinoSnapshot.key
                                        ?: ""
                            }

                            lista.add(destino)
                        }
                    }

                    adapter.actualizarLista(
                        lista
                    )

                    if (lista.isEmpty()) {

                        binding.tvEmptyList.visibility =
                            View.VISIBLE

                    } else {

                        binding.tvEmptyList.visibility =
                            View.GONE
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    binding.tvEmptyList.text =
                        error.message

                    binding.tvEmptyList.visibility =
                        View.VISIBLE
                }
            }
        )
    }

    private fun abrirFormulario(
        destinoId: String?
    ) {

        val intent =
            Intent(
                this,
                FormDestinoActivity::class.java
            )

        if (destinoId != null) {

            intent.putExtra(
                "destinoId",
                destinoId
            )
        }

        startActivity(intent)
    }

    private fun confirmarEliminar(
        destino: Destino
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                R.string.dialog_delete_title
            )

            .setMessage(
                getString(
                    R.string.dialog_delete_message,
                    destino.nombre
                )
            )

            .setPositiveButton(
                R.string.btn_yes
            ) { _, _ ->

                eliminarDestino(
                    destino.id
                )
            }

            .setNegativeButton(
                R.string.btn_cancel,
                null
            )

            .show()
    }

    private fun eliminarDestino(
        id: String
    ) {

        destinosRef
            .child(id)
            .removeValue()
            .addOnFailureListener { error ->

                AlertDialog.Builder(this)
                    .setTitle(
                        R.string.error_title
                    )
                    .setMessage(
                        error.localizedMessage
                            ?: getString(
                                R.string.error_delete
                            )
                    )
                    .setPositiveButton(
                        android.R.string.ok,
                        null
                    )
                    .show()
            }
    }

    override fun onCreateOptionsMenu(
        menu: Menu
    ): Boolean {

        menu.add(
            0,
            MENU_LOGOUT,
            0,
            R.string.menu_logout
        )

        return true
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {

        if (
            item.itemId ==
            MENU_LOGOUT
        ) {

            auth.signOut()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()

            return true
        }

        return super.onOptionsItemSelected(
            item
        )
    }

    companion object {

        private const val MENU_LOGOUT = 1
    }
}