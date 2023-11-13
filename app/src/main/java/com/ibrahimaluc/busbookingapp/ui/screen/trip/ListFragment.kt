package com.ibrahimaluc.busbookingapp.ui.screen.trip

import android.util.Log
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.ibrahimaluc.busbookingapp.data.remote.Trip
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibrahimaluc.busbookingapp.core.base.BaseFragment
import com.ibrahimaluc.busbookingapp.core.extensions.collectLatestLifecycleFlow
import com.ibrahimaluc.busbookingapp.data.remote.MapItem
import com.ibrahimaluc.busbookingapp.databinding.FragmentListBinding
import com.ibrahimaluc.busbookingapp.ui.adapter.TripListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : BaseFragment<ListViewModel, FragmentListBinding>(
    ListViewModel::class.java,
    FragmentListBinding::inflate
) {
    private val args: ListFragmentArgs by navArgs()
    private var tripListAdapter: TripListAdapter? = null


    override fun onCreateViewInvoke() {
        val mapItems = args.trip
        adapter(mapItems.trips)
        collectLatestLifecycleFlow(viewModel.state, ::handleViewState)
    }

    private fun adapter(tripList: List<Trip>?) = with(binding) {
        tripListAdapter = TripListAdapter(::book)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = tripListAdapter
        tripListAdapter!!.tripList = tripList ?: emptyList()
    }

    private fun handleViewState(listUiState: ListUiState) {
        listUiState.message?.let {
            if (it.isNotEmpty()) {
                showDialog()
            }
        }
        if (listUiState.trip != null) {
            navigateToMapFragment(args.trip)
        }
    }

    private fun book(id: Int?) {
        val selectedStation = args.trip.id
        if (selectedStation != null) {
            if (id != null) {
                viewModel.sendBook(6, selectedStation, id)
            }
        }
    }

    private fun showDialog() {
        try {
            val dialogFragment = TripDialogFragment()
            val fragmentManager = parentFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.add(dialogFragment, "trip_dialog")
            transaction.commit()
        } catch (e: Exception) {
            Log.e("TripDialogFragment", "Error while showing dialog", e)
        }
    }

    private fun navigateToMapFragment(bookedSt:MapItem) {
        val action = ListFragmentDirections.actionListFragmentToMapsFragment(bookedSt)
        println("book: $bookedSt")
        findNavController().navigate(action)
    }
}