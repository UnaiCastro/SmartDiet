package com.tfg.smartdiet.iu.PaginaPrincipal.Alimento

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tfg.smartdiet.R
import com.tfg.smartdiet.domain.Alimento

class AlimentoAdapter(private var alimentos: MutableList<Alimento>) :
    RecyclerView.Adapter<AlimentoViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlimentoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alimento, parent, false)
        return AlimentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlimentoViewHolder, position: Int) {
        val item = alimentos[position]
        holder.render(item)
    }



    override fun getItemCount()=alimentos.size

    fun setAlimentos(alimentos: MutableList<Alimento>){
        this.alimentos=alimentos
    }

}
