package com.fayyad.siskamlingapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fayyad.siskamlingapp.data.ReportModel
import com.fayyad.siskamlingapp.databinding.ItemReportBinding

class ReportAdapter(
    private val reportList: ArrayList<ReportModel>,
    // Menambahkan detektor klik (callback)
    private val onVerifyClick: (ReportModel) -> Unit,
    private val onDeleteClick: (ReportModel) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]

        holder.binding.tvTitikRawan.text = report.titikRawan
        holder.binding.tvKategori.text = "Kategori: ${report.kategoriKejadian}"
        holder.binding.tvJadwal.text = "Jadwal Ronda: ${report.jadwalRonda}"
        holder.binding.chipStatus.text = report.statusVerifikasiRw

        // Mengatur tampilan berdasarkan status
        if (report.statusVerifikasiRw == "Telah Diverifikasi") {
            holder.binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_dark)
            holder.binding.btnVerify.visibility = View.GONE // Sembunyikan tombol jika sudah diverifikasi
        } else {
            holder.binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_orange_dark)
            holder.binding.btnVerify.visibility = View.VISIBLE
        }

        if (report.fotoKejadian.isNotEmpty()) {
            holder.binding.ivFotoKejadian.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load(report.fotoKejadian).centerCrop().into(holder.binding.ivFotoKejadian)
        } else {
            holder.binding.ivFotoKejadian.visibility = View.GONE
        }

        // Aksi saat tombol ditekan
        holder.binding.btnVerify.setOnClickListener { onVerifyClick(report) }
        holder.binding.btnDelete.setOnClickListener { onDeleteClick(report) }
    }

    override fun getItemCount(): Int = reportList.size

    fun updateData(newList: List<ReportModel>) {
        reportList.clear()
        reportList.addAll(newList)
        notifyDataSetChanged()
    }
}