package com.example.geos

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * Adapter para mostrar la lista de usuarios en un RecyclerView.
 *
 * @property listaUsuarios Lista mutable de usuarios que se mostrarán.
 * @property onEliminarClick Lambda que se ejecuta al presionar el botón de eliminar un usuario.
 */
class UsuarioAdapter(
    private val listaUsuarios: MutableList<Usuario>,
    private val onEliminarClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    /**
     * ViewHolder que representa cada elemento de usuario en la lista.
     *
     * @property tvNombre TextView que muestra el nombre del usuario.
     * @property tvInmueble TextView que muestra el inmueble del usuario.
     * @property tvUbicacion TextView que muestra la ubicación del usuario (latitud y longitud).
     * @property ivFoto ImageView que muestra la foto del usuario.
     */
    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvInmueble: TextView = itemView.findViewById(R.id.tvInmueble)
        val tvUbicacion: TextView = itemView.findViewById(R.id.tvUbicacion)
        val ivFoto: ImageView = itemView.findViewById(R.id.ivFoto)
    }

    /**
     * Infla el layout de cada elemento de usuario y crea el ViewHolder correspondiente.
     *
     * @param parent ViewGroup padre.
     * @param viewType Tipo de vista (no usado, puede ser ignorado).
     * @return Un UsuarioViewHolder que contiene la vista del elemento.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    /**
     * Vincula los datos de un usuario específico a los elementos del ViewHolder.
     *
     * @param holder ViewHolder que contiene los elementos de la UI.
     * @param position Posición del usuario en la lista.
     */
    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.tvNombre.text = "Usuario: ${usuario.nombreUsuario}"
        holder.tvInmueble.text = "Inmueble: ${usuario.inmueble}"
        holder.tvUbicacion.text = "Ubicación: Lat ${usuario.latitud}, Lng ${usuario.longitud}"

        // Cargar foto con Glide
        Glide.with(holder.itemView.context)
            .load(Uri.parse(usuario.fotoUri))
            .placeholder(R.drawable.camera_logo)
            .into(holder.ivFoto)

        // Configurar clic en botón eliminar
        holder.itemView.findViewById<ImageView>(R.id.btnEliminar).setOnClickListener {
            onEliminarClick(usuario)
        }
    }

    /**
     * Devuelve la cantidad de elementos en la lista de usuarios.
     *
     * @return Número de usuarios en la lista.
     */
    override fun getItemCount(): Int = listaUsuarios.size

    /**
     * Actualiza la lista de usuarios del adapter.
     *
     * Se usa, por ejemplo, cuando se realiza un filtrado.
     *
     * @param nuevaLista Lista con los usuarios que se deben mostrar.
     */
    fun actualizarLista(nuevaLista: List<Usuario>) {
        listaUsuarios.clear()
        listaUsuarios.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
