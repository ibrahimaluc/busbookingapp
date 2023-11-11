package com.ibrahimaluc.busbookingapp.ui.screen.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibrahimaluc.busbookingapp.R
import com.ibrahimaluc.busbookingapp.data.remote.Trip
import androidx.navigation.fragment.navArgs



class ListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tripList = arguments?.getParcelableArrayList<Trip>("tripList")
        println(tripList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

}