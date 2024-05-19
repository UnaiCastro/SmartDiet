package com.tfg.smartdiet.iu.PaginaPrincipal.Dieta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tfg.smartdiet.databinding.FragmentPorridgeRecipeBinding

class PorridgeRecipe : Fragment() {
    private var _binding: FragmentPorridgeRecipeBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPorridgeRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PorridgeRecipe()
    }
}