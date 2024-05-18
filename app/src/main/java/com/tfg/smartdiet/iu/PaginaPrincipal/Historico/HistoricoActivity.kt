package com.tfg.smartdiet.iu.PaginaPrincipal.Historico

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.tfg.smartdiet.R
import com.tfg.smartdiet.databinding.ActivityHistoricoBinding

class HistoricoActivity : AppCompatActivity() {
    companion object {
        fun navigateTo(context:Context) {
            val intent = Intent(context, HistoricoActivity::class.java) // navegamos a historico
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityHistoricoBinding
    private lateinit var adapter: FragmentPageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoBinding.inflate(layoutInflater) //Tener la vista y la activity conectadas directamente
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        adapter = FragmentPageAdapter(supportFragmentManager , lifecycle)

        binding.PartidoTabLayout.addTab(binding.PartidoTabLayout.newTab().setText("Completadas"))
        binding.PartidoTabLayout.addTab(binding.PartidoTabLayout.newTab().setText("Fallidas"))

        binding.PartidosTabLayoutViewPager.adapter = adapter

        binding.PartidoTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.PartidosTabLayoutViewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        binding.PartidosTabLayoutViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.PartidoTabLayout.selectTab(binding.PartidoTabLayout.getTabAt(position))

            }
        })
    }
}