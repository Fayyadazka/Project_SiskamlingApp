package com.fayyad.siskamlingapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.databinding.ItemReportBinding

class ReportAdapter(
    private val isRwMode: Boolean, // Mode RW atau Warga
    private val onUpdateStatus: (ReportModel, String) -> Unit,
    private val onDelete: (ReportModel) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private val reports = mutableListOf<ReportModel>()

    fun submitList(newList: List<ReportModel>) {
        reports.clear()
        reports.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }

    override fun getItemCount(): Int = reports.size

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: ReportModel) {
            binding.tvTitikRawan.text = report.titikRawan
            binding.tvKategori.text = "Kategori: ${report.kategoriKejadian}"
            binding.tvJadwal.text = "Jadwal Ronda: ${report.jadwalRonda}"
            binding.chipStatus.text = report.statusVerifikasiRw

            // Load Gambar
            // Load Gambar
            val imageUrl = report.fotoKejadian.trim()
            if (imageUrl.isNotEmpty() && imageUrl.startsWith("http")) {
                binding.ivFotoKejadian.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .override(800, 600)
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(binding.ivFotoKejadian)
            } else {
                binding.ivFotoKejadian.visibility = View.GONE
            }

            // --- LOGIKA ROLE WAKTU DIPANGGIL ---
            if (isRwMode) {
                // Jika Mode RW: Tampilkan Tombol Aksi
                binding.btnVerify.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE

                binding.btnVerify.setOnClickListener {
                    onUpdateStatus(report, "Diverifikasi RW")
                }
                binding.btnDelete.setOnClickListener {
                    onDelete(report)
                }
            } else {
                // Jika Mode Warga: SEMBUNYIKAN Tombol Aksi Verifikasi & Hapus
                binding.btnVerify.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }
    }
}