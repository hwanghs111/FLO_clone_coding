package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), LoginView {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로그인 화면에서 회원가입을 누르면 signupactivity로 가도록
        binding.loginSignUpTv.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        binding.loginSignInBtn.setOnClickListener{
            login()

        }
    }

//    private fun login(){
//        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()){
//            Toast.makeText(this,"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if(binding.loginPasswordEt.text.toString().isEmpty()){
//            Toast.makeText(this,"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val email: String = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
//        val pwd: String = binding.loginPasswordEt.text.toString()
//
//        val songDB = SongDatabase.getInstance(this)!!
//
//        //UserDao에서 만든 유저 일치하는지 확인 하는 함수: getUser
//        val user = songDB.userDao().getUser(email, pwd)
//
//
//        //DB에서 입력한 회원정보와 같은 정보를 찾았을때
//        user?.let{
//            Log.d("LOGINACT/GET_USER","userId: ${user.id},$user")
//            //발급받은 jwt를 저장해주는 함수
//            saveJwt(user.id)
//        }
//        if(user == null) {
//            //DB에서 입력한 회원정보와 같은 정보를 찾지 못했을때
//            Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }


    private fun login(){
        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginDirectInputEt.text.toString().isEmpty()){
            Toast.makeText(this,"이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.loginPasswordEt.text.toString().isEmpty()){
            Toast.makeText(this,"비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val email = binding.loginIdEt.text.toString() + "@" + binding.loginDirectInputEt.text.toString()
        val pwd = binding.loginPasswordEt.text.toString()
        val user = User(email, pwd, "")

        val authService = AuthService()
        authService.setLoginView(this)

        authService.login(user)
    }
    //로그인 하면 메인으로 들어가게끔
    private fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
    }

    override fun onLoginLoading(){
        binding.loginLoadingPb.visibility = View.VISIBLE
    }

    override fun onLoginSuccess(auth: Auth) {
        binding.loginLoadingPb.visibility = View.GONE
        saveJwt(this, auth.jwt)
        saveUserIdx(this, auth.userIdx)

        startMainActivity()
    }

    override fun onLoginFailure(code: Int, message: String) {
        binding.loginLoadingPb.visibility = View.GONE

        when(code){
            2015, 2019, 3014 -> {
                binding.loginErrorTv.visibility = View.VISIBLE
                binding.loginErrorTv.text = message
            }
        }
    }

    //이거 정확히 먼지모르겠음
//    private fun saveJwt(jwt: Int){
//        val spf = getSharedPreferences("auth", MODE_PRIVATE)
//        val editor = spf.edit()
//
//        editor.putInt("jwt",jwt)
//        editor.apply()
//    }
}