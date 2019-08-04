package com.example.twitterclone

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import java.security.AccessControlContext

class HomeActivity : AppCompatActivity() {

    private val firebaseAuth=FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
    companion object {
        fun newIntent(context: Context)=Intent(context, HomeActivity::class.java)
    }

    fun onLogout(v: View){
        firebaseAuth.signOut()
        startActivity(LoginActivity.newIntent(this))
        finish()
    }
}
