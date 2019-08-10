package com.example.twitterclonenew.fragments

import android.content.Context
import android.support.v4.app.Fragment
import com.example.twitterclonenew.adapters.TweetListAdapter
import com.example.twitterclonenew.listeners.HomeCallback
import com.example.twitterclonenew.listeners.TwitterListenerImpl
import com.example.twitterclonenew.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

abstract class TwitterFragment : Fragment() {
    protected var tweetsAdapter: TweetListAdapter? = null
    protected var currentUser: User? = null
    protected val firebaseDB = FirebaseFirestore.getInstance()
    protected val userId = FirebaseAuth.getInstance().currentUser?.uid
    protected var listener: TwitterListenerImpl? = null
    protected var callback: HomeCallback? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is HomeCallback) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement HomeCallback")
        }
    }

    fun setUser(user: User?) {
        this.currentUser = user
        listener?.user = user
    }

    abstract fun updateList()

    override fun onResume() {
        super.onResume()
        updateList()
    }
}