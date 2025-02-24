package com.example.rojgarhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rojgarhub.databinding.ItemJobBinding
import com.example.rojgarhub.model.JobModel

class JobsAdapter(private val onJobClicked: (JobModel) -> Unit) :
    ListAdapter<JobModel, JobsAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }

    inner class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: JobModel) {
            binding.apply {
                tvJobTitle.text = job.title
                tvJobLocation.text = job.location
                tvJobSalary.text = job.salary
                root.setOnClickListener { onJobClicked(job) }
            }
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<JobModel>() {
        override fun areItemsTheSame(oldItem: JobModel, newItem: JobModel): Boolean {
            return oldItem.jobId == newItem.jobId
        }

        override fun areContentsTheSame(oldItem: JobModel, newItem: JobModel): Boolean {
            return oldItem == newItem
        }
    }
}