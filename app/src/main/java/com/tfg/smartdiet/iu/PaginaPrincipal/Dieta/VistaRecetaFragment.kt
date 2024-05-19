package com.tfg.smartdiet.iu.PaginaPrincipal.Dieta

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.FragmentVistaRecetaBinding


class VistaRecetaFragment : Fragment() {


    private var _binding: FragmentVistaRecetaBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVistaRecetaBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener { redirigir(1) }
        binding.imageView2.setOnClickListener { redirigir(2) }

    }

    private fun redirigir(destination: Number) {
        if (destination == 1) {
            findNavController().navigate(R.id.actionProteinMilkshakeRecipe)
        }
        else {
            findNavController().navigate(R.id.actionPorridgeRecipe)
        }


    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VistaRecetaFragment()
    }
}