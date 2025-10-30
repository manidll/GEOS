package com.example.geos

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UsuarioAdapter(private val listaUsuarios: List<Usuario>) :
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvInmueble: TextView = itemView.findViewById(R.id.tvInmueble)
        val tvUbicacion: TextView = itemView.findViewById(R.id.tvUbicacion)
        val ivFoto: ImageView = itemView.findViewById(R.id.ivFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.tvNombre.text = usuario.nombreUsuario
        holder.tvInmueble.text = usuario.inmueble
        holder.tvUbicacion.text = "Lat: ${usuario.latitud}, Lng: ${usuario.longitud}"

        // Cargar foto si existe
        Glide.with(holder.itemView.context)
            .load(Uri.parse(usuario.fotoUri))
            .placeholder(R.drawable.camera_logo) // agrega un ícono base en drawable
            .into(holder.ivFoto)
    }

    override fun getItemCount(): Int = listaUsuarios.size
}
