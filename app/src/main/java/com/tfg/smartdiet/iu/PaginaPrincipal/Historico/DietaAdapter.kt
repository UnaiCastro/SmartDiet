package com.tfg.smartdiet.iu.PaginaPrincipal.Historico

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfg.smartdiet.R
import com.tfg.smartdiet.domain.Dieta

class DietaAdapter(private var dietas: MutableList<Dieta>) :
    RecyclerView.Adapter<DietaViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DietaViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dieta, parent, false)
        return DietaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DietaViewHolder, position: Int) {
        val item = dietas[position]
        holder.render(item)
    }

    override fun getItemCount()=dietas.size

    fun setDietas(dietas:MutableList<Dieta>){
        this.dietas=dietas
    }

}
