package com.baseballmatching.app.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baseballmatching.app.MainActivity
import com.baseballmatching.app.database.DBKey.Companion.DB_USERS
import com.baseballmatching.app.databinding.ActivitySignInBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignInActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var age: String
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.length < 4) {
                return@setOnClickListener
            } else if (!email.contains("@")) {
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 회원가입 성공
                        Toast.makeText(this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        // 회원가입 실패
                        Toast.makeText(this, "이미 가입된 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.btnSignIn.setOnClickListener {
            email = binding.etEmail.text.toString()
            password = binding.etPassword.text.toString()
            userName = binding.etUserName.text.toString()
            age = binding.etAge.text.toString()
            gender = binding.etGender.text.toString()


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (password.length < 4) {
                return@setOnClickListener
            } else if (!email.contains("@")) {
                return@setOnClickListener
            }

            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = mutableMapOf<String, Any>()
                        user["userId"] = Firebase.auth.currentUser!!.uid
                        user["email"] = email
                        user["userName"] = userName
                        user["age"] = age
                        user["gender"] = gender

                        Firebase.database.reference.child(DB_USERS).child(Firebase.auth.currentUser!!.uid).updateChildren(user).addOnSuccessListener {
                            Log.d("firebase user", "user data update success")
                        }.addOnFailureListener {
                            Log.d("firebase user", "user data update failed")
                            Log.e("firebase user", it.toString())
                        }

                        // 로그인 성공
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // 로그인 실패
                        Log.e("LoginActivity", "로그인에 실패했습니다.", task.exception)
                        Toast.makeText(this, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}