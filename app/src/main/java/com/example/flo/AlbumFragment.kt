package com.example.flo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

//상속받는 이 fragment라는 클래스는 안드로이드에서 fragment들의 기능들 자유롭게 사용할수있도록 하는 클래스
class AlbumFragment : Fragment() {

    lateinit var binding : FragmentAlbumBinding
    private var gson : Gson = Gson()

    val information = arrayListOf("수록곡","상세정보","영상")

    private var isLiked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //fragment에서 binding을 초기화하는 하나의 패턴으로 이해
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        //홈에서 넘어온 데이터 받아오기
        val albumData = arguments?.getString("album")
        val album = gson.fromJson(albumData, Album::class.java)
        isLiked = isLikedAlbum(album.id)

        // 홈에서 넘어온 데이터를 반영
        setViews(album)
        setClickListeners(album)


        //ROOM_DB
        val songs = getSongs(album.id) //앨범안에 있는 수록곡들을 불러옵니다.
        // 이 다음에 수록곡 프래그먼트에 songs을 전달해주는 식으로 사용하시면 됩니다.


        //set click listener
        binding.albumBtnLeftArrowIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        //토스트메세지 - 짧은 메세지를 담은 팝업창. 잠깐 뜨는
//        binding.albumAlbumCardView.setOnClickListener{
//            Toast.makeText(activity,"라일락",Toast.LENGTH_SHORT).show()
//        }


        val albumAdapter = AlbumVPAdapter(this)

        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp){
            tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    private fun setViews(album: Album) {
        binding.albumTitleTv.text = album.title.toString()
        binding.albumSingerTv.text = album.singer.toString()
        binding.albumAlbumIv.setImageResource(album.coverImg!!)

        //하트 이미지 바꿔주기
        if(isLiked){
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun setClickListeners(album : Album){
        val userId: Int = getUserIdx(requireContext())

        binding.albumLikeIv.setOnClickListener{
            if(isLiked){
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(userId, album.id)
            }else{
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
        }
    }

    //앨범 좋아요 눌렀을때 db에 저장하는 과정
    private fun likeAlbum(userId: Int, albumId: Int){
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)
        //db에 추가
        songDB.albumDao().likeAlbum((like))
    }

    private fun isLikedAlbum(albumId: Int):Boolean{
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getUserIdx(requireContext())

        val likeId: Int? = songDB.albumDao().isLikeAlbum(userId,albumId)

        //앨범안에 id가 존재하면 likeId != null -> true 없으면 false를 반환하게 되는
        return likeId != null
    }

    private fun disLikedAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        songDB.albumDao().disLikeAlbum(userId, albumId)
    }

//    private fun getUserIdx(): Int{
//        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
//
//        return spf!!.getInt("jwt", 0)
//    }

    //ROOM_DB
    private fun getSongs(albumIdx: Int): ArrayList<Song>{
        val songDB = SongDatabase.getInstance(requireContext())!!

        val songs = songDB.songDao().getSongsInAlbum(albumIdx) as ArrayList

        return songs
    }
}