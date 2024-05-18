package com.tfg.smartdiet.iu.PaginaPrincipal.Alimento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.R
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
        initListener()
        val callback = object :
            OnBackPressedCallback(true) { //Funcion para que el boton e atras no funcione y solamente se pueda salir dandole a cerrar sesion
            override fun handleOnBackPressed() {
                // Mostrar un Toast con el mensaje
                Toast.makeText(requireContext(), getString(R.string.porFavorCierreSesion), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        // Agregar el callback al lifecycle owner del fragmento
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initListener() {

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.chipVerdura.id -> {
                    this.aliemntoRV=binding.AlimentosRV
                    this.aliemntoRV.layoutManager=LinearLayoutManager(context)
                    alimentoAdapter = AlimentoAdapter(obtenerAlimentosVerdura())
                    this.aliemntoRV.adapter=alimentoAdapter
                    // Aquí puedes añadir más acciones para esta opción
                }
                binding.chipFruta.id-> {
                    this.aliemntoRV=binding.AlimentosRV
                    this.aliemntoRV.layoutManager=LinearLayoutManager(context)
                    alimentoAdapter = AlimentoAdapter(obtenerAlimentosFruta())
                    this.aliemntoRV.adapter=alimentoAdapter
                    // Aquí puedes añadir más acciones para esta opción
                }
                binding.chipGranos.id-> {
                    this.aliemntoRV=binding.AlimentosRV
                    this.aliemntoRV.layoutManager=LinearLayoutManager(context)
                    alimentoAdapter = AlimentoAdapter(obtenerAlimentosGrano())
                    this.aliemntoRV.adapter=alimentoAdapter
                    // Aquí puedes añadir más acciones para esta opción
                }
                binding.chipLacteos.id-> {
                    this.aliemntoRV=binding.AlimentosRV
                    this.aliemntoRV.layoutManager=LinearLayoutManager(context)
                    alimentoAdapter = AlimentoAdapter(obtenerAlimentosLacteos())
                    this.aliemntoRV.adapter=alimentoAdapter
                    // Aquí puedes añadir más acciones para esta opción
                }
                binding.chipProteinas.id -> {
                    this.aliemntoRV=binding.AlimentosRV
                    this.aliemntoRV.layoutManager=LinearLayoutManager(context)
                    alimentoAdapter = AlimentoAdapter(obtenerAlimentosProteina())
                    this.aliemntoRV.adapter=alimentoAdapter

                    // Aquí puedes añadir más acciones para esta opción
                }
                View.NO_ID -> {
                    this.aliemntoRV=binding.AlimentosRV
                    this.aliemntoRV.layoutManager=LinearLayoutManager(context)
                    alimentoAdapter = AlimentoAdapter(obtenerAlimentos())
                    this.aliemntoRV.adapter=alimentoAdapter
                    // Aquí puedes añadir más acciones para esta opción
                }
            }
        }
    }

    private fun initUI() {

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

    private fun obtenerAlimentosLacteos(): MutableList<Alimento> {
        val alimentosListLacteos= mutableListOf<Alimento>()
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
                if (tipo=="productos lacteos"){
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
                    alimentosListLacteos.add(alimento)
                    println(alimentosListLacteos)
                }
            }
            alimentoAdapter.apply {
                setAlimentos(alimentosListLacteos)
                notifyDataSetChanged()
            }
        }
        return alimentosListLacteos
    }

    private fun obtenerAlimentosProteina(): MutableList<Alimento> {
        val alimentosListProteina= mutableListOf<Alimento>()
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
                if (tipo=="proteinas"){
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
                    alimentosListProteina.add(alimento)
                    println(alimentosListProteina)
                }


            }
            alimentoAdapter.apply {
                setAlimentos(alimentosListProteina)
                notifyDataSetChanged()
            }
        }
        return alimentosListProteina
    }

    private fun obtenerAlimentosGrano(): MutableList<Alimento> {
        val alimentosListGranos= mutableListOf<Alimento>()
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
                if (tipo=="granos"){
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
                    alimentosListGranos.add(alimento)
                    println(alimentosListGranos)
                }
            }
            alimentoAdapter.apply {
                setAlimentos(alimentosListGranos)
                notifyDataSetChanged()
            }
        }
        return alimentosListGranos
    }

    private fun obtenerAlimentosFruta(): MutableList<Alimento> {
        val alimentosListFruta= mutableListOf<Alimento>()
        val db=FirebaseFirestore.getInstance()
        db.collection("alimentos").get().addOnSuccessListener {
            for (document in it){
                val tipo=document.data["tipo"].toString()
                if (tipo == "Fruta"){
                    val agua=document.data["agua"].toString()
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
                    alimentosListFruta.add(alimento)
                    println(alimentosListFruta)
                }

            }
            alimentoAdapter.apply {
                setAlimentos(alimentosListFruta)
                notifyDataSetChanged()
            }
        }
        return alimentosListFruta
    }

    private fun obtenerAlimentosVerdura(): MutableList<Alimento> {
        val alimentosListVerdura= mutableListOf<Alimento>()
        val db=FirebaseFirestore.getInstance()
        db.collection("alimentos").get().addOnSuccessListener {
            for (document in it){
                val tipo=document.data["tipo"].toString()
                if (tipo == "verdura"){
                    val agua=document.data["agua"].toString()
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
                    alimentosListVerdura.add(alimento)
                    println(alimentosListVerdura)
                }

            }
            alimentoAdapter.apply {
                setAlimentos(alimentosListVerdura)
                notifyDataSetChanged()
            }
        }
        return alimentosListVerdura
    }

}