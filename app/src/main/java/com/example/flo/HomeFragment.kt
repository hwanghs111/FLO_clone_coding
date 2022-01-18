package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.databinding.FragmentHomeBinding
import com.google.gson.Gson


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private var albums = ArrayList<Album>()

    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //ROOM_DB
        songDB = SongDatabase.getInstance(requireContext())!!
        albums.addAll(songDB.albumDao().getAlbums()) // songDB에서 album list를 가져옵니다.

        //레이아웃 매니저 설정
        binding.homeTodayMusicAlbumRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        //더미데이터랑 Adapter 연결
        val albumRecyclerViewAdapter = AlbumRVAdapter(albums)


        //리스너 객체 생성 및 전달
        albumRecyclerViewAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener{
            override fun onItemClick(album: Album) {
                startAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRecyclerViewAdapter.removeItem(position)
            }
        })

        //리사이클러뷰에 어댑터를 연결
        binding.homeTodayMusicAlbumRecyclerview.adapter = albumRecyclerViewAdapter


        //초기화는 새로운 어댑터 클래스를 만들어줘서 어댑터 클래스를 배너어댑터라는 변수에 넣어줘야함
        val bannerAdapter = HomeViewpagerAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))

        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    fun startAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

}