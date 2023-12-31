package com.baseballmatching.app.ui.matching

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.baseballmatching.app.ui.matching.child.MatchedUsersFragment
import com.baseballmatching.app.ui.matching.child.MatchingRequestedUsersFragment
import com.baseballmatching.app.ui.matching.child.MatchingUsersFragment

class ViewpagerFragmentAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    // 1. ViewPager2에 연결할 Fragment 들을 생성
    val fragmentList = listOf<Fragment>(MatchingUsersFragment(), MatchingRequestedUsersFragment(), MatchedUsersFragment())

    // 2. ViesPager2에서 노출시킬 Fragment 의 갯수 설정
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    // 3. ViewPager2의 각 페이지에서 노출할 Fragment 설정
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}