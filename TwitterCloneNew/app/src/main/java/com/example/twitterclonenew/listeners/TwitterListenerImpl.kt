package com.example.twitterclonenew.listeners

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import com.example.twitterclonenew.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TwitterListenerImpl(val tweetList: RecyclerView, var user: User?, val callBack: HomeCallback?) : TweetListener {

    protected val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onLayoutClick(tweet: Tweet?) {
        tweet?.let {
            val owner = tweet.userIds?.get(0)
            if (owner != userId) {
                if (user?.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Unfollow ${tweet.username}?")
                        .setPositiveButton("yes") { dialog, which ->
                            tweetList.isClickable = false
                            var followedUsers = user?.followUsers
                            if (followedUsers == null) {
                                followedUsers = arrayListOf()
                            }
                            followedUsers?.remove(owner)
                            firebaseDB.collection(DATA_USERS).document(userId!!).update(DATA_USER_FOLLOW, followedUsers)
                                .addOnSuccessListener {
                                    tweetList.isClickable = true
                                    callBack?.onUserUpdate()
                                }
                                .addOnFailureListener {
                                    tweetList.isClickable = false
                                }
                        }
                        .setNegativeButton("cancel") { dialog, which -> }
                        .show()
                } else {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Follow ${tweet.username}?")
                        .setPositiveButton("yes") { dialog, which ->
                            tweetList.isClickable = false
                            var followedUsers = user?.followUsers
                            if (followedUsers == null) {
                                followedUsers = arrayListOf()
                            }
                            owner?.let {
                                followedUsers?.add(owner)
                                firebaseDB.collection(DATA_USERS).document(userId!!)
                                    .update(DATA_USER_FOLLOW, followedUsers)
                                    .addOnSuccessListener {
                                        tweetList.isClickable = true
                                        callBack?.onUserUpdate()
                                    }
                                    .addOnFailureListener {
                                        tweetList.isClickable = false
                                    }
                            }

                        }
                        .setNegativeButton("cancel") { dialog, which -> }
                        .show()
                }
            }
        }
    }

    override fun onLike(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val likes = tweet.likes
            if (tweet.likes?.contains(userId) == true) {
                likes?.remove(userId)
            } else {
                likes?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEETS_LIKES, likes)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callBack?.onRefresh()
                }
                .addOnFailureListener {
                    tweetList.isClickable = false
                }
        }
    }

    override fun onRetweet(tweet: Tweet?) {
        tweet?.let {
            tweetList.isClickable = false
            val retweets = tweet.userIds
            if (retweets?.contains(userId) == true) {
                retweets?.remove(userId)
            } else {
                retweets?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEET_USER_IDS, retweets)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callBack?.onRefresh()
                }
                .addOnFailureListener {
                    tweetList.isClickable = true
                }
        }
    }
}