// Tanggal Pengerjaan: 2 Agustus 2026
// NIM: 10121054
// Nama: Fayyad Azka Muhammad
// Kelas: IF12K

package com.fayyad.siskamlingapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.fayyad.siskamlingapp.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Menahan layar selama 2000 milidetik (2 detik)
        Handler(Looper.getMainLooper()).postDelayed({
            // Berpindah ke MainActivity setelah 2 detik
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Tutup SplashActivity agar pengguna tidak bisa kembali ke layar ini saat menekan tombol "Back"
            finish()
        }, 2000)
    }
}