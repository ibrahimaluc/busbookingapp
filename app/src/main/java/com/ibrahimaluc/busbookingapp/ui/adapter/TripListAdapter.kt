package com.ibrahimaluc.busbookingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ibrahimaluc.busbookingapp.data.remote.MapItem
import com.ibrahimaluc.busbookingapp.data.remote.Trip
import com.ibrahimaluc.busbookingapp.databinding.ItemListBinding

class TripListAdapter(private val clickControl: (Int?) -> Unit
) :
    RecyclerView.Adapter<TripListAdapter.TripListViewHolder>() {

    class TripListViewHolder(var binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallBack = object : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip) =
            oldItem == newItem
    }
    private val listDiffer = AsyncListDiffer(this, diffCallBack)

    var tripList: List<Trip>
        get() = listDiffer.currentList
        set(value) = listDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripListViewHolder {
        return TripListViewHolder(
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TripListViewHolder, position: Int) {
        holder.binding.data = tripList[position]
        holder.binding.btBook.setOnClickListener {
            clickControl(tripList[position].id)
        }
    }

    override fun getItemCount(): Int {
        return tripList.size
    }
}