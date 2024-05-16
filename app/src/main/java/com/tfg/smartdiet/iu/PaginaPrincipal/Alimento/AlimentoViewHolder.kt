package com.tfg.smartdiet.iu.PaginaPrincipal.Alimento

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tfg.smartdiet.databinding.ItemAlimentoBinding
import com.tfg.smartdiet.domain.Alimento

class AlimentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ItemAlimentoBinding.bind(itemView)
    fun render(alimento: Alimento) {
        val url = alimento.imagen
        Glide.with(itemView.context).load(url).into(binding.AlimentoFoto)
        binding.AlimentoNombre.text=alimento.nombre

    }
}
