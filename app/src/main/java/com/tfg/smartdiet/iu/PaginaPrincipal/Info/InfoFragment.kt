package com.tfg.smartdiet.iu.PaginaPrincipal.Info

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.tfg.smartdiet.R
import com.tfg.smartdiet.iu.PaginaPrincipal.Historico.HistoricoActivity

class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn = view.findViewById<Button>(R.id.BTNHistorico)
        btn.setOnClickListener {
            val i= Intent(context,HistoricoActivity::class.java)
            startActivity(i)
        }
    }

}