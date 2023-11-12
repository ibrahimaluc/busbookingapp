package com.ibrahimaluc.busbookingapp.ui.screen.trip

import android.media.MediaCodec.LinearBlock
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibrahimaluc.busbookingapp.R
import com.ibrahimaluc.busbookingapp.data.remote.Trip
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrahimaluc.busbookingapp.core.base.BaseFragment
import com.ibrahimaluc.busbookingapp.databinding.FragmentListBinding
import com.ibrahimaluc.busbookingapp.ui.adapter.TripListAdapter


class ListFragment : BaseFragment<ListViewModel,FragmentListBinding>(
    ListViewModel::class.java,
    FragmentListBinding::inflate
) {

    private  var tripList: ArrayList<Trip> = arrayListOf()
    private var tripListAdapter:TripListAdapter? = null

    override fun onCreateViewInvoke() {
        tripList= arguments?.getParcelableArrayList("tripList")!!
        adapter(tripList)
    }

    private fun adapter(tripList: ArrayList<Trip>?)= with(binding){
        tripListAdapter= TripListAdapter()
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        recyclerView.adapter=tripListAdapter
        tripListAdapter!!.tripList = tripList ?: emptyList()
    }
}