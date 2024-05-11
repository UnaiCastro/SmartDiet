package com.tfg.smartdiet.iu.PaginaPrincipal.Alimento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tfg.smartdiet.databinding.FragmentSegundaBinding

class AlimentoFragment : Fragment() {

    private var _binding: FragmentSegundaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSegundaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


}