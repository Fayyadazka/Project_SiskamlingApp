package com.fayyad.siskamlingapp.data

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class ReportModel(
    var id: String = "",

    @SerializedName("titikRawan", alternate = ["titik_rawan"])
    var titikRawan: String = "",

    @SerializedName("jadwalRonda", alternate = ["jadwal_ronda"])
    var jadwalRonda: String = "",

    @SerializedName("kategoriKejadian", alternate = ["kategori_kejadian"])
    var kategoriKejadian: String = "",

    @SerializedName("fotoKejadian", alternate = ["foto_kejadian"])
    var fotoKejadian: String = "",

    @SerializedName("statusVerifikasiRw", alternate = ["status_verifikasi_rw"])
    var statusVerifikasiRw: String = "Belum Diverifikasi"
)