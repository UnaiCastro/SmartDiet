package com.tfg.smartdiet.iu.PaginaPrincipal.Alimento

import android.content.Intent
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
        itemView.setOnClickListener{
            val i= Intent(itemView.context,AlimentoInformacionActivity::class.java)
            i.putExtra("nombre",alimento.nombre)
            i.putExtra("imagen",alimento.imagen)
            i.putExtra("agua",alimento.agua)
            i.putExtra("calorias",alimento.calorias)
            i.putExtra("carbohidratos",alimento.carbohidratos)
            i.putExtra("descripcion",alimento.descripcion)
            i.putExtra("grasas",alimento.grasas)
            i.putExtra("minerales",alimento.minerales)
            i.putExtra("proteinas",alimento.proteinas)
            i.putExtra("tipo",alimento.tipo)
            i.putExtra("vitaminas",alimento.vitaminas)
            itemView.context.startActivity(i)

        }

    }
}
