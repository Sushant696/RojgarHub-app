// ApplicationsAdapter.kt
package com.example.rojgarhub.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rojgarhub.databinding.ItemApplicationBinding
import com.example.rojgarhub.model.ApplicationModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApplicationsAdapter :
    ListAdapter<ApplicationModel, ApplicationsAdapter.ApplicationViewHolder>(ApplicationDiffCallback()) {

    private var onStatusUpdateListener: ((ApplicationModel, String) -> Unit)? = null

    fun setOnStatusUpdateListener(listener: (ApplicationModel, String) -> Unit) {
        onStatusUpdateListener = listener
    }

    inner class ApplicationViewHolder(private val binding: ItemApplicationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(application: ApplicationModel) {
            binding.apply {
                tvApplicantName.text = application.applicantName
                tvJobTitle.text = application.jobTitle
                tvApplicationDate.text = "Applied: ${formatDate(application.applicationDate)}"
                tvStatus.text = application.status.uppercase()
                tvStatus.setTextColor(getStatusColor(application.status))

                // Only show buttons for pending applications
                if (application.status == "pending") {
                    btnAccept.visibility = View.VISIBLE
                    btnReject.visibility = View.VISIBLE
                } else {
                    btnAccept.visibility = View.GONE
                    btnReject.visibility = View.GONE
                }

                btnAccept.setOnClickListener {
                    onStatusUpdateListener?.invoke(application, "accepted")
                }

                btnReject.setOnClickListener {
                    onStatusUpdateListener?.invoke(application, "rejected")
                }
            }
        }

        private fun formatDate(timestamp: Long): String {
            return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
        }

        private fun getStatusColor(status: String): Int {
            return when (status.lowercase()) {
                "pending" -> Color.parseColor("#FFA500")
                "accepted" -> Color.parseColor("#2ECC71")
                "rejected" -> Color.parseColor("#E74C3C")
                else -> Color.BLACK
            }
        }
    }
    class ApplicationDiffCallback : DiffUtil.ItemCallback<ApplicationModel>() {
        override fun areItemsTheSame(oldItem: ApplicationModel, newItem: ApplicationModel): Boolean {
            return oldItem.applicationId == newItem.applicationId
        }

        override fun areContentsTheSame(oldItem: ApplicationModel, newItem: ApplicationModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}