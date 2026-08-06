package com.fayyad.siskamlingapp.data

data class ReportModel(
    var id: String = "",
    val titikRawan: String = "",
    val jadwalRonda: String = "",
    val kategoriKejadian: String = "",
    val fotoKejadian: String = "", // Menyimpan URL gambar dari Storage
    var statusVerifikasiRw: String = "Belum Diverifikasi"
)