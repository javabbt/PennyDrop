package dev.mfazio.pennydrop.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.mfazio.pennydrop.R
import dev.mfazio.pennydrop.types.PlayerSummary
import dev.mfazio.pennydrop.databinding.PlayerSummaryListItemBinding

class PlayerSummaryAdapter :
    ListAdapter<PlayerSummary, PlayerSummaryAdapter.PlayerSummaryViewHolder>(
        PlayerSummaryDiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerSummaryViewHolder =
        PlayerSummaryViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.player_summary_list_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(viewHolder: PlayerSummaryViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    inner class PlayerSummaryViewHolder(private val binding: PlayerSummaryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlayerSummary) {
            binding.apply {
                playerSummary = item
                executePendingBindings()
            }
        }
    }
}

private class PlayerSummaryDiffCallback : DiffUtil.ItemCallback<PlayerSummary>() {

    override fun areItemsTheSame(oldItem: PlayerSummary, newItem: PlayerSummary): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: PlayerSummary, newItem: PlayerSummary): Boolean =
        oldItem == newItem
}