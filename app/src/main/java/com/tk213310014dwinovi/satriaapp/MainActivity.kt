package com.tk213310014dwinovi.satriaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val petaLok = findViewById<Button>(R.id.petaLok)
        petaLok.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        // Kode di dalam activity saat ini
        val keluar = findViewById<Button>(R.id.keluar)
        keluar.setOnClickListener {
            finish() // Mengakhiri activity saat ini dan kembali ke halaman sebelumnya
        }
    }
}