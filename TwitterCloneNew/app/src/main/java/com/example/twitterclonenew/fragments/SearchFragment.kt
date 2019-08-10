package com.example.twitterclonenew.fragments


import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.twitterclonenew.R
import com.example.twitterclonenew.adapters.TweetListAdapter
import com.example.twitterclonenew.listeners.TwitterListenerImpl
import com.example.twitterclonenew.util.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : TwitterFragment() {

    private var currentHashTag = ""
    private var hashTagFollowed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener = TwitterListenerImpl(tweetList, currentUser, callback)

        tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
        tweetsAdapter?.setListener(listener)
        tweetList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            updateList()
        }



        followHashtag.setOnClickListener {
            followHashtag.isClickable = false
            val followed = currentUser?.followHashtags
            if (hashTagFollowed) {
                followed?.remove(currentHashTag)

            } else {
                followed?.add(currentHashTag)
            }
            firebaseDB.collection(DATA_USERS).document(userId).update(DATA_USER_HASTAGS, followed)
                .addOnSuccessListener {
                    callback?.onUserUpdate()
                    followHashtag.isClickable = true

                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    followHashtag.isClickable = true

                }

        }
    }

    fun newHashTag(term: String) {
        currentHashTag = term
        followHashtag.visibility = View.VISIBLE

        updateList()
    }

    override fun updateList() {
        tweetList?.visibility = View.GONE
        firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, currentHashTag).get()
            .addOnSuccessListener { list ->
                tweetList?.visibility = View.VISIBLE
                val tweets = arrayListOf<Tweet>()
                for (document in list.documents) {
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let { tweets.add(it) }
                }
                val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
                tweetsAdapter?.updateTweets(sortedTweets)

            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
        updateFollowDrowable()
    }

    private fun updateFollowDrowable() {
        hashTagFollowed = currentUser?.followHashtags?.contains(currentHashTag) == true
        context?.let {
            if (hashTagFollowed) {
                followHashtag.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.follow))
            } else {
                followHashtag.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.follow_inactive))
            }
        }
    }
}
