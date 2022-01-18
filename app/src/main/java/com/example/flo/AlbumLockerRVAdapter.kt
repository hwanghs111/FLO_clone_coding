package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemLockerAlbumBinding

class AlbumLockerRVAdapter() : RecyclerView.Adapter<AlbumLockerRVAdapter.ViewHolder> (){
    private val albums = ArrayList<Album>()
    // 클릭 인터페이스 정의
    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }

    //리스너 객체를 전달받는 함수랑 리스너 객체를 저장할 변수
    private lateinit var myItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        myItemClickListener = itemClickListener
    }

    // 뷰홀더를 생성해줘야 할 때 호출되는 함수 -> 아이템 뷰 객체를 만들어서 뷰홀더에 던져줌
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AlbumLockerRVAdapter.ViewHolder {
        val binding: ItemLockerAlbumBinding = ItemLockerAlbumBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }

    // 뷰홀더에 데이터를 바인딩해줘야 할 때마다 호출되는 함수 -> 엄청나게 많이 호출됨
    override fun onBindViewHolder(holder: AlbumLockerRVAdapter.ViewHolder, position: Int) {
        holder.bind(albums[position])
        holder.binding.itemAlbumMoreIv.setOnClickListener {
            myItemClickListener.onRemoveSong(albums[position].id)
            removeSong(position)
        }
    }
    //데이터 세트 크기를 알려주는 함수 -> 리사이클러뷰가 마지막이 언제인지를 알게 된다.
    //이게 있어야 리사이클러뷰가 정상적으로 작동함
    override fun getItemCount(): Int = albums.size

    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: ArrayList<Album>) {
        this.albums.clear()
        this.albums.addAll(albums)

        notifyDataSetChanged()
    }

    fun removeSong(position: Int){
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    //뷰홀더
    inner class ViewHolder(val binding: ItemLockerAlbumBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(album: Album){
            binding.itemAlbumTitleTv.text = album.title
            binding.itemAlbumSingerTv.text = album.singer
            binding.itemAlbumCoverImgIv.setImageResource(album.coverImg!!)
        }

    }


}