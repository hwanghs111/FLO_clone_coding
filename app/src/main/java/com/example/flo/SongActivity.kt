package com.example.flo

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityMainBinding
import com.example.flo.databinding.ActivitySongBinding
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import kotlin.text.*

//뷰바인딩 사용방법: 먼저 바인딩이라는 것을 선언 전역변수에 선언해줘야하는데
//lateinit: 정방선언? 선언을 지금할건데 나중에 초기화 꼭 해줄게
//var 변수명 & val 변수명 ->둘의 차이? var은 처음에 초기화해주고 나중에 값변경 가능 val은 나중에 값변경 불가능
//var 변수명 : 자료형 = 초기화할 값
class SongActivity : AppCompatActivity() {
    lateinit var binding: ActivitySongBinding
    // 미디어 플레이어
    private var mediaPlayer: MediaPlayer? = null
    lateinit var timer : Timer

    private var songs = ArrayList<Song>()
    private var nowPos = 0
    private lateinit var songDB: SongDatabase

    //onCreate라는 함수가 액티비티가 처음 생성이 될때 처음으로 실행되는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding 초기화 시키는 하나의 틀?
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()

    }

    override fun onPause() {
        super.onPause()

        songs[nowPos].second = (songs[nowPos].playTime * binding.songPlayerSb.progress) / 1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt("songId", songs[nowPos].id)
        editor.apply()
    }

    //이 songActivity 화면이 꺼지면 이 함수가 자동으로 실행이 됨
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()//즉 화면이 꺼지면 쓰레드가 꺼짐
        mediaPlayer?.release() //미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어플레이어 해제
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    private fun initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID",songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song) {
        val music = resources.getIdentifier(song.music, "raw", this.packageName)

        binding.songTitleTv.text = song.title
        binding.songSingerTv.text = song.singer
        binding.songStartTimeTv.text =
            String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text =
            String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songPlayerSb.progress = (song.second * 1000 / song.playTime)
        setPlayerStatus(song.isPlaying)

        if (song.isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        mediaPlayer = MediaPlayer.create(this, music)
    }

    private fun initClickListener() {
        binding.songBtnDownArrowIv.setOnClickListener {
            finish()
        }

        binding.songBtnMiniplayerIv.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songBtnPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songBtnPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        binding.songBtnNextIv.setOnClickListener {
            moveSong(+1)
        }

        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }

    }
    private fun moveSong(direct: Int){

        if (nowPos + direct < 0){
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size){
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        timer.interrupt()
        startTimer()

        mediaPlayer?.release() // 미디어플레이어가 가지고 있던 리소스를 해방
        mediaPlayer = null // 미디어플레이어 해제

        setPlayer(songs[nowPos])
    }
    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike,songs[nowPos].id)

        if (isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }
    }



    //함수를 사용하는 이유: 직관적으로 이해 쉽고 코드의 가독성도 높아짐
    private fun setPlayerStatus(isPlaying : Boolean){
        timer.isPlaying = isPlaying
        songs[nowPos].isPlaying = isPlaying

        if(isPlaying){
            binding.songBtnMiniplayerIv.visibility = View.GONE
            binding.songBtnPauseIv.visibility = View.VISIBLE

            mediaPlayer?.start()
        }else{
            binding.songBtnMiniplayerIv.visibility = View.VISIBLE
            binding.songBtnPauseIv.visibility = View.GONE

            mediaPlayer?.pause()
        }
    }

    inner class Timer(private val playTime: Int = 0,var isPlaying:Boolean = false) : Thread(){
        private var second: Long = 0

        @SuppressLint("SetTextI18n")
        override fun run() {
            try{
                while (true) {
                    if(second >= playTime){
                        break
                    }

                    if(isPlaying){
                        sleep(1000)//sleep은 thread에서 제공하는 함수
                        second++

                        runOnUiThread(){//위에 handler 지우고 handler.post대신 runOnUiThread만 써줘도 ㄱㅊ
                            binding.songPlayerSb.progress =
                                (second * 1000 / playTime).toInt()
                            binding.songStartTimeTv.text = String.format(
                                "%02d:%02d",
                                TimeUnit.SECONDS.toMinutes(second),
                                second%60)
                        }
                    }

                }
            }catch (e : InterruptedException){
                Log.d("SONG","쓰레드가 종료되었습니다. ${e.message}")
            }

        }
    }

}