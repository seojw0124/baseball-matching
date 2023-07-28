package com.baseballmatching.app.ui.matching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.FragmentMatchingBinding

class MatchingFragment: Fragment() {

    private lateinit var binding: FragmentMatchingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matching, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMatchingBinding.bind(view)


    }
}