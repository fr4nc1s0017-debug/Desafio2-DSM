package udb.edu.sv.dsm.agenciaviajes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.udb.agenciaviajes.databinding.ItemDestinoBinding
import com.udb.agenciaviajes.model.Destino
import com.udb.agenciaviajes.utils.ImageUtils

class DestinoAdapter(
    private var destinos: List<Destino>,
    private val onItemClick: (Destino) -> Unit,
    private val onDeleteClick: (Destino) -> Unit
) : RecyclerView.Adapter<DestinoAdapter.DestinoViewHolder>() {

    inner class DestinoViewHolder(
        val binding: ItemDestinoBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinoViewHolder {

        val binding = ItemDestinoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DestinoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DestinoViewHolder,
        position: Int
    ) {

        val destino = destinos[position]

        with(holder.binding) {

            tvNombre.text = destino.nombre

            tvPrecio.text =
                "$${"%.2f".format(destino.precio)} · ${destino.pais}"

            tvDescripcion.text = destino.descripcion

            val bitmap =
                ImageUtils.base64ToBitmap(
                    destino.imagenBase64
                )

            if (bitmap != null) {
                Glide.with(ivDestino.context)
                    .load(bitmap)
                    .into(ivDestino)
            }

            root.setOnClickListener {
                onItemClick(destino)
            }

            btnEliminar.setOnClickListener {
                onDeleteClick(destino)
            }
        }
    }

    override fun getItemCount(): Int =
        destinos.size

    fun actualizarLista(
        nuevaLista: List<Destino>
    ) {
        destinos = nuevaLista
        notifyDataSetChanged()
    }
}