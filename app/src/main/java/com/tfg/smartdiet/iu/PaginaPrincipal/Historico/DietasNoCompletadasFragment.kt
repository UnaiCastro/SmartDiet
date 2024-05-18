package com.tfg.smartdiet.iu.PaginaPrincipal.Historico

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tfg.smartdiet.databinding.FragmentDietasNoCompletadasBinding
import com.tfg.smartdiet.domain.Dieta

class DietasNoCompletadasFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore
    private lateinit var dietaAdapter: DietaAdapter
    private val dietas = obtenerDietasNoCompletadas()



    private lateinit var dietasRV: RecyclerView

    private var _binding: FragmentDietasNoCompletadasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDietasNoCompletadasBinding.inflate(layoutInflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        initRV()
    }

    private fun initRV() {
        this.dietasRV=binding.DietasNoCompletadasRV
        dietasRV.layoutManager = LinearLayoutManager(context)
        dietaAdapter = DietaAdapter(this.dietas)
        dietasRV.adapter = dietaAdapter
    }

    private fun obtenerDietasNoCompletadas(): MutableList<Dieta> {
        db= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        val dietasNoCompletadasList= mutableListOf<Dieta>()
        val user=auth.currentUser!!.uid
        println(user)
        db.collection("dietas").whereEqualTo("userID",user).get().addOnSuccessListener {
            for (document in it){
                val calAct = document.data!!["caloriasAct"].toString()
                println(calAct)
                val calObj = document.data!!["caloriasObj"].toString()
                println(calObj)
                val carboAct = document.data!!["carbohidratosAct"].toString()
                println(carboAct)
                val carboObj = document.data!!["carbohidratosObj"].toString()
                println(carboObj)
                val grasasAct = document.data!!["grasasAct"].toString()
                println(grasasAct)
                val grasasObj = document.data!!["grasasObj"].toString()
                println(grasasObj)
                val proAct = document.data!!["proteinasAct"].toString()
                println(proAct)
                val proObj = document.data!!["proteinasObj"].toString()
                println(proObj)
                val fecha = document.data!!["fecha"].toString()
                val tolerance = 0.10

                if (
                    calAct.toInt() !in (calObj.toInt() * (1 - tolerance)).toInt()..(calObj.toInt() * (1 + tolerance)).toInt() ||
                    carboAct.toInt() !in (carboObj.toInt() * (1 - tolerance)).toInt()..(carboObj.toInt() * (1 + tolerance)).toInt() ||
                    grasasAct.toInt() !in (grasasObj.toInt() * (1 - tolerance)).toInt()..(grasasObj.toInt() * (1 + tolerance)).toInt() ||
                    proAct.toInt() !in (proObj.toInt() * (1 - tolerance)).toInt()..(proObj.toInt() * (1 + tolerance)).toInt()
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
                        user

                    )
                    dietasNoCompletadasList.add(dieta)
                }
                dietaAdapter.apply {
                    setDietas(dietasNoCompletadasList)
                    notifyDataSetChanged()
                }
            }
        }
        return dietasNoCompletadasList
    }

}