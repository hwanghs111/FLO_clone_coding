package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

//어댑터안에는 데이터를 넣을 수 있는 프래그먼트를 넣어주도록 할게요
class HomeViewpagerAdapter (fragment : Fragment) : FragmentStateAdapter(fragment){
    //private는 이 클래스 안에서만 쓰겠다는 의미
    private val fragmentlist : ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = fragmentlist.size
    //position은 0~(숫자-1) position에 있는 fragment를 만들어줄것이다 라는 의미
    //위에 사이즈만큼 실행

    override fun createFragment(position: Int): Fragment = fragmentlist[position]

    fun addFragment(fragment: Fragment){
        fragmentlist.add(fragment)
        notifyItemInserted(fragmentlist.size - 1)//배열은 0부터 세잖아.근데 사이즈로 따지면 +1이 되니까 -1을 해주는것
    }
}