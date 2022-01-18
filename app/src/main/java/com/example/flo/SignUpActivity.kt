package com.example.flo

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.math.sign

class SignUpActivity : AppCompatActivity(), SignUpView {
    lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //가입버튼을 눌렀을 때 signUp이 진행이 되어야함
        binding.signUpSingUpBtn.setOnClickListener{
            signUp()
           //회원가입 정상적으로 하면 다시 로그인 액티비티로 돌아가도록 꺼지게끔
        }

    }

    private fun getUser(): User{
        val email: String = binding.signUpIdEt.text.toString() + "@" + binding.signUpDirectInputEt.text.toString()
        val pwd: String = binding.signUpPasswordEt.text.toString()
        val name: String = binding.signUpNameEt.text.toString()

        return User(email, pwd, name)
    }
//validation처리
//    private fun signUp(){
//        if(binding.signUpIdEt.text.toString().isEmpty() || binding.signUpDirectInputEt.text.toString().isEmpty()){
//            Toast.makeText(this,"이메일 형식이잘못되었습니다.",Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if(binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()){
//            Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
//            return
//        }
//        //위에를 통과했으면 회원가입때 입력한 값들을 DB에 저장해주는
//        val userDB = SongDatabase.getInstance(this)!!
//        userDB.userDao().insert(getUser())
//        //DB에 저장이됐는지 확인하는 log
//        val users = userDB.userDao().getUsers()
//
//        Log.d("SIGUPACT",users.toString())
//    }

    private fun signUp(){
        if(binding.signUpIdEt.text.toString().isEmpty() || binding.signUpDirectInputEt.text.toString().isEmpty()){
            Toast.makeText(this,"이메일 형식이 잘못되었습니다.",Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.signUpNameEt.text.toString().isEmpty()){
            Toast.makeText(this,"이름 형식이 잘못되었습니다.",Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.signUpPasswordEt.text.toString() != binding.signUpPasswordCheckEt.text.toString()){
            Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
            return
        }

        //val retrofit = Retrofit.Builder().baseUrl("http://13.125.121.202").build()

        val authService = AuthService()
        authService.setSignUpView(this)

        authService.singUp(getUser())

        Log.d("SIGNUPACT/ASYNC", "Hello")
    }

    override fun onSignUpLoading() {
        binding.signUpLoadingPb.visibility = View.VISIBLE
    }

    override fun onSignUpSuccess() {
        binding.signUpLoadingPb.visibility = View.GONE

        finish()
    }

    override fun onSignUpFailure(code: Int, message: String) {
        binding.signUpLoadingPb.visibility = View.GONE

        when(code){
            2000 -> {}
            2016, 2017 -> {
                binding.signUpEmailErrorTv.visibility = View.VISIBLE
                binding.signUpEmailErrorTv.text = message
            }
        }
    }


}