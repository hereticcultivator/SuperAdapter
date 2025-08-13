package com.heretic_cultivator.super_adapter.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.heretic_cultivator.super_adapter.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).also {
            binding = it
            setContentView(it.root)
        }
        
        setupClickListeners()

        binding.root.postDelayed({
            startActivity(Intent(this, SuperAdapterDemoActivity::class.java))
        }, 2000)
    }
    
    private fun setupClickListeners() {
        binding.btnSuperAdapterDemo.setOnClickListener {
            startActivity(Intent(this, SuperAdapterDemoActivity::class.java))
        }
    }

}