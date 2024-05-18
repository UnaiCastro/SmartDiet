package com.tfg.smartdiet.iu.PaginaPrincipal.Historico

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.databinding.FragmentDietasCompletadasBinding
import com.tfg.smartdiet.domain.Dieta

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class DietasCompletadasFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var dietaAdapter: DietaAdapter
    private val dietas = obtenerDietas()
    private lateinit var dietasRV: RecyclerView

    private var _binding: FragmentDietasCompletadasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDietasCompletadasBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initRV()
    }

    private fun initRV() {
        this.dietasRV=binding.DietasCompletadasRV
        dietasRV.layoutManager = LinearLayoutManager(context)
        dietaAdapter = DietaAdapter(this.dietas)
        dietasRV.adapter = dietaAdapter
        Log.i("DietasCompletadas", "estoy en initRV")


    }

    private fun obtenerDietas(): MutableList<Dieta> {
        db= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        val dietasCompletadasList= mutableListOf<Dieta>()
        val user=auth.currentUser!!.uid
        db.collection("dietas").whereEqualTo("userID", user).get().addOnSuccessListener {
            for (document in it.documents){
                val calAct = document.data!!["caloriasAct"].toString()
                val calObj = document.data!!["caloriasObj"].toString()
                val carboAct = document.data!!["carbohidratosAct"].toString()
                val carboObj = document.data!!["carbohidratosObj"].toString()
                val grasasAct = document.data!!["grasasAct"].toString()
                val grasasObj = document.data!!["grasasObj"].toString()
                val proAct = document.data!!["proteinasAct"].toString()
                val proObj = document.data!!["proteinasObj"].toString()
                val fecha = document.data!!["fecha"].toString()
                val usuario = document.data!!["userID"].toString()
                val tolerance = 0.10

                Log.i("DietasCompletadas", "Calorias Actuales: $calAct")
                Log.i("DietasCompletadas", "Calorias Objetivo: $calObj")
                Log.i("DietasCompletadas", "Carbohidratos Actuales: $carboAct")
                Log.i("DietasCompletadas", "Carbohidratos Objetivo: $carboObj")
                Log.i("DietasCompletadas", "Grasas Actuales: $grasasAct")
                Log.i("DietasCompletadas", "Grasas Objetivo: $grasasObj")
                Log.i("DietasCompletadas", "Proteinas Actuales: $proAct")
                Log.i("DietasCompletadas", "Proteinas Objetivo: $proObj")
                Log.i("DietasCompletadas", "Fecha: $fecha")
                Log.i("DietasCompletadas", "Usuario ID: $usuario")
                if (
                    calAct.toInt() in (calObj.toInt() * (1 - tolerance)).toInt()..(calObj.toInt() * (1 + tolerance)).toInt() &&
                    carboAct.toInt() in (carboObj.toInt() * (1 - tolerance)).toInt()..(carboObj.toInt() * (1 + tolerance)).toInt() &&
                    grasasAct.toInt() in (grasasObj.toInt() * (1 - tolerance)).toInt()..(grasasObj.toInt() * (1 + tolerance)).toInt() &&
                    proAct.toInt() in (proObj.toInt() * (1 - tolerance)).toInt()..(proObj.toInt() * (1 + tolerance)).toInt()
                ){
                    val dieta = Dieta(
                        calAct,
                        calObj,
                        carboAct,
                        carboObj,
                        grasasAct,
                        grasasObj,
                        proAct,
                        proObj,
                        fecha,
                        usuario

                    )
                    dietasCompletadasList.add(dieta)
                }
                dietaAdapter.apply {
                    setDietas(dietasCompletadasList)
                    notifyDataSetChanged()
                }
            }

        }
        return dietasCompletadasList
    }


}