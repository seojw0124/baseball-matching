package com.baseballmatching.app.ui.matching

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.baseballmatching.app.R
import com.baseballmatching.app.databinding.FragmentMatchingBinding
import com.baseballmatching.app.ui.matching.child.ViewpagerFragmentAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MatchingFragment: Fragment() {

    private lateinit var binding: FragmentMatchingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        binding = FragmentMatchingBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val viewPager: ViewPager2 = binding.viewPager2
        val tabLayout = binding.tabLayout

        // ViewPager2에 Adapter 연결
        val viewpagerFragmentAdapter = ViewpagerFragmentAdapter(this)
        viewPager.adapter = viewpagerFragmentAdapter

        val tabTitles = listOf<String>("매칭 신청", "받은 요청", "매칭 완료")

        // TabLayout에 ViewPager2 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}