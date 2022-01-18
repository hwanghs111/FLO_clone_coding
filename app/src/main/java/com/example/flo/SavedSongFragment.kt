package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLockerSavedsongBinding
import com.google.gson.Gson

class SavedSongFragment : Fragment() {

    lateinit var binding : FragmentLockerSavedsongBinding
    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview(){
        binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdapter = SongRVAdapter()
        //리스너 객체 생성 및 전달

        songRVAdapter.setMyItemClickListener(object : SongRVAdapter.MyItemClickListener{
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false,songId)
            }
        })

        binding.lockerSavedSongRecyclerView.adapter = songRVAdapter

        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList)

    }



//        val saveRVAdapter = SaveRVAdapter(saveDatas)
//
//
//    //리사이클러뷰에 어댑터를 연결
//        binding.saveSongRecyclerview.adapter = saveRVAdapter
//
//        saveRVAdapter.setMyItemClickListener(object : SaveRVAdapter.MyItemClickListener{
//
//            override fun onItemClick(save: Save) {
//                changeSaveFragment(save)
//            }
//
//            private fun changeSaveFragment(save: Save) {
//                (context as MainActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_frm, SavedSongFragment().apply {
//                        arguments = Bundle().apply {
//                            val gson = Gson()
//                            val saveJson = gson.toJson(save)
//                            putString("save", saveJson)
//                        }
//                    })
//                    .commitAllowingStateLoss()
//            }
//        })
//        //레이아웃 매니저 설정
//        binding.saveSongRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//        return binding.root
//    }
}