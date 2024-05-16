package com.tfg.smartdiet.iu.PaginaPrincipal.Alimento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.databinding.FragmentSegundaBinding
import com.tfg.smartdiet.domain.Alimento

class AlimentoFragment : Fragment() {

    private var _binding: FragmentSegundaBinding? = null
    private val binding get() = _binding!!
    private lateinit var aliemntoRV:RecyclerView
    private lateinit var alimentoAdapter: AlimentoAdapter
    private val alimentos=obtenerAlimentos()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSegundaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        //crearAlimentos()
        initRV()
    }

    private fun initRV() {
        this.aliemntoRV=binding.AlimentosRV
        this.aliemntoRV.layoutManager=LinearLayoutManager(context)
        alimentoAdapter = AlimentoAdapter(this.alimentos)
        aliemntoRV.adapter=alimentoAdapter
    }

    private fun obtenerAlimentos(): MutableList<Alimento> {
        val aliemntosList= mutableListOf<Alimento>()
        val db=FirebaseFirestore.getInstance()
        db.collection("alimentos").get().addOnSuccessListener {
            for (document in it){
                val agua=document.data["agua"].toString()
                val calorias=document.data["calorias"].toString()
                val carbohidratos=document.data["carbohidratos"].toString()
                val descripcion=document.data["descripcion"].toString()
                val grasas=document.data["grasas"].toString()
                val imagen=document.data["imagen"].toString()
                val minerales=document.data["minerales"].toString()
                val nombre=document.data["nombre"].toString()
                val proteinas=document.data["proteinas"].toString()
                val tipo=document.data["tipo"].toString()
                val vitaminas=document.data["vitaminas"].toString()

                val alimento=Alimento(
                    agua,
                    calorias,
                    carbohidratos,
                    descripcion,
                    grasas,
                    imagen,
                    minerales,
                    nombre,
                    proteinas,
                    tipo,
                    vitaminas
                )
                aliemntosList.add(alimento)
                println(aliemntosList)
            }
            alimentoAdapter.apply {
                setAlimentos(aliemntosList)
                notifyDataSetChanged()
            }
        }
        return aliemntosList
    }

    /*private fun crearAlimentos() {
        val db=FirebaseFirestore.getInstance()
        db.collection("alimentos").whereEqualTo("nombre","Aguacate").get().addOnSuccessListener {
            for (document in it){
                val agua = document.data["agua"].toString()
                val calorias=document.data["calorias"].toString()
                val carbohidratos=document.data["carbohidratos"].toString()
                val descripcion=document.data["descripcion"].toString()
                val grasas=document.data["grasas"].toString()
                val imagen=document.data["imagen"].toString()
                val minerales=document.data["minerales"].toString()
                val nombre=document.data["nombre"].toString()
                val proteinas=document.data["proteinas"].toString()
                val vitaminas=document.data["vitaminas"].toString()

                val alimento=Alimento(
                    agua,calorias,carbohidratos,descripcion,grasas,imagen,minerales,nombre,proteinas,"verdura",vitaminas

                ).toMap()
                val alimento1=Alimento(
                    agua,calorias,carbohidratos,descripcion,grasas,imagen,minerales,nombre,proteinas,"proteinas",vitaminas

                ).toMap()

                val alimento2=Alimento(
                    agua,calorias,carbohidratos,descripcion,grasas,imagen,minerales,nombre,proteinas,"productos lacteos",vitaminas

                ).toMap()

                val alimento3=Alimento(
                    agua,calorias,carbohidratos,descripcion,grasas,imagen,minerales,nombre,proteinas,"granos",vitaminas

                ).toMap()

                db.collection("alimentos").add(alimento)
                db.collection("alimentos").add(alimento2)
                db.collection("alimentos").add(alimento3)
                db.collection("alimentos").add(alimento1)



            }
        }
    }*/


}