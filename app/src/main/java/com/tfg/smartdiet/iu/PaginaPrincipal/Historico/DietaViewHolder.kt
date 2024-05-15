package com.tfg.smartdiet.iu.PaginaPrincipal.Historico

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tfg.smartdiet.databinding.ItemDietaBinding
import com.tfg.smartdiet.domain.Dieta

class DietaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
    private val binding = ItemDietaBinding.bind(itemView)
    fun render(dieta: Dieta) {
        val caloriasObj=dieta.caloriasObj
        val carbohidratosObj=dieta.carbohidratosObj
        val grasasObj=dieta.grasasObj
        val proteinasObj=dieta.proteinasObj
        val userID=dieta.userID
        val caloriasAct=dieta.caloriasAct
        val carbohidratosAct=dieta.carbohidratosAct
        val grasasAct=dieta.grasasAct
        val proteinasAct=dieta.proteinasAct
        val fecha=dieta.fecha

        binding.ItemDietaFecha.text=fecha
        binding.ItemCalorias.text="$caloriasAct/$caloriasObj"
        binding.ItemCarbos.text="$carbohidratosAct/$carbohidratosObj"
        binding.ItemGrasas.text="$grasasAct/$grasasObj"
        binding.ItemProtes.text="$proteinasAct/$proteinasObj"
        /*binding.ItemDietaQuien.text=*/

    }


}
