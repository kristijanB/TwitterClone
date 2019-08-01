package com.example.twitterclone

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.security.AccessControlContext

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
    companion object {
        fun newIntent(context: Context)=Intent(context, HomeActivity::class.java)
    }
}
