package com.tfg.smartdiet.iu.PaginaPrincipal.Dieta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.FragmentProteinMilkshakeDetailBinding
import com.tfg.smartdiet.databinding.FragmentVistaRecetaBinding


class fragment_protein_milkshake_detail : Fragment() {

    private var _binding: FragmentProteinMilkshakeDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProteinMilkshakeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_protein_milkshake_detail().apply {}

    }
}