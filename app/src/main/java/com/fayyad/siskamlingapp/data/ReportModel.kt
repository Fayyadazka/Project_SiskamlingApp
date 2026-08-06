package com.fayyad.siskamlingapp.data

data class ReportModel(
    val id: String = "",
    val titikRawan: String = "",
    val jadwalRonda: String = "",
    val kategoriKejadian: String = "",
    val fotoKejadian: String = "", // Menyimpan URL gambar dari Storage
    val statusVerifikasiRw: String = "Belum Diverifikasi"
)