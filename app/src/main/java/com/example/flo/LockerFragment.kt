package com.example.flo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator


class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    val information = arrayListOf("저장한 곡","음악파일","저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerVp.adapter = lockerAdapter
        TabLayoutMediator(binding.lockerTb, binding.lockerVp){
            tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener{
            startActivity(Intent(activity,LoginActivity::class.java))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initView()
    }

    private fun initView(){
        val jwt = getUserIdx()

        if(jwt == 0){
            binding.lockerLoginTv.text = "로그인"

            binding.lockerLoginTv.setOnClickListener{
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }else{
            binding.lockerLoginTv.text = "로그아웃"

            binding.lockerLoginTv.setOnClickListener{
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    //jwt를 가져오는 함수
    private fun getUserIdx(): Int{
        val spf = activity?.getSharedPreferences("auth",AppCompatActivity.MODE_PRIVATE)

        return spf!!.getInt("userIdx", 0)
    }

    //로그아웃 시켜주는 함수
    private fun logout(){
        val spf = activity?.getSharedPreferences("auth",AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("userIdx")
        editor.apply()
    }
}