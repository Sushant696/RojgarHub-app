package com.example.rojgarhub.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rojgarhub.databinding.ItemApplicationBinding
import com.example.rojgarhub.model.ApplicationModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApplicationsAdapter(
    private val isEmployer: Boolean,
    private val onStatusUpdate: (ApplicationModel, String) -> Unit
) : ListAdapter<ApplicationModel, ApplicationsAdapter.ViewHolder>(ApplicationDiffCallback()) {

    inner class ViewHolder(val binding: ItemApplicationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(application: ApplicationModel) {
            binding.apply {
                tvJobTitle.text = application.jobTitle
                tvApplicant.text = if (isEmployer) application.applicantName else application.jobTitle
                tvDate.text = formatDate(application.applicationDate)
                tvStatus.text = application.status.uppercase()
                tvStatus.setTextColor(getStatusColor(application.status))

                if (isEmployer) {
                    setupStatusSpinner(application)
                    statusSpinner.visibility = View.VISIBLE
                    btnUpdate.visibility = View.VISIBLE
                } else {
                    statusSpinner.visibility = View.GONE
                    btnUpdate.visibility = View.GONE
                }
            }
        }

        private fun setupStatusSpinner(application: ApplicationModel) {
            val statusOptions = arrayOf("approved", "pending", "rejected")
            val spinnerAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                statusOptions
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            binding.statusSpinner.adapter = spinnerAdapter
            binding.statusSpinner.setSelection(statusOptions.indexOf(application.status))

            binding.btnUpdate.setOnClickListener {
                val newStatus = binding.statusSpinner.selectedItem.toString()
                onStatusUpdate(application, newStatus)
            }
        }

        private fun formatDate(timestamp: Long): String {
            return SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(timestamp))
        }

        private fun getStatusColor(status: String): Int {
            return when (status.lowercase()) {
                "pending" -> Color.parseColor("#FFA500")
                "approved" -> Color.parseColor("#2ECC71")
                "rejected" -> Color.parseColor("#E74C3C")
                else -> Color.BLACK
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApplicationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ApplicationDiffCallback : DiffUtil.ItemCallback<ApplicationModel>() {
        override fun areItemsTheSame(oldItem: ApplicationModel, newItem: ApplicationModel): Boolean {
            return oldItem.applicationId == newItem.applicationId
        }

        override fun areContentsTheSame(oldItem: ApplicationModel, newItem: ApplicationModel): Boolean {
            return oldItem == newItem
        }
    }
}