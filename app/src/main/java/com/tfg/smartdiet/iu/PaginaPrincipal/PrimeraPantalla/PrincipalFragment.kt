package com.tfg.smartdiet.iu.PaginaPrincipal.PrimeraPantalla

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.FragmentPrincipalBinding

class PrincipalFragment : Fragment() {


    private var _binding: FragmentPrincipalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPrincipalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


}