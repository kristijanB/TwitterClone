package com.example.twitterclonenew.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.twitterclonenew.R
import com.example.twitterclonenew.util.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth=FirebaseAuth.getInstance()
    private val firebaseDB=FirebaseFirestore.getInstance()
    private val userId=FirebaseAuth.getInstance().currentUser?.uid
    private val fireseStorage =FirebaseStorage.getInstance().reference
    private var imageUrl: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if(userId==null){
            finish()
        }

        profileProgressLayout.setOnTouchListener { v, event -> true }

        photoIV.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        populateInfo()
    }


    fun populateInfo(){
        profileProgressLayout.visibility=View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                usernameET.setText(user?.username, TextView.BufferType.EDITABLE)
                emailET.setText(user?.email, TextView.BufferType.EDITABLE)
                imageUrl=user?.imageUrl
                imageUrl?.let {
                    photoIV.loadUrl(user?.imageUrl, R.drawable.logo)
                }
                profileProgressLayout.visibility=View.GONE
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                finish()
            }
    }

    fun onApply(v: View){
        profileProgressLayout.visibility=View.VISIBLE
        val username=usernameET.text.toString()
        val email=emailET.text.toString()
        val map=HashMap<String, Any>()
        map[DATA_USER_USERNAME]=username
        map[DATA_USER_EMAIL]=email

        firebaseDB.collection(DATA_USERS).document(userId!!).update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show()
                profileProgressLayout.visibility=View.GONE
                finish()
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(this, "Update faild. Please try again", Toast.LENGTH_SHORT).show()
                profileProgressLayout.visibility=View.GONE
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode== REQUEST_CODE_PHOTO){
            storeImage(data?.data)
        }
    }

    fun storeImage(imageUri: Uri?){
        imageUri?.let {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            profileProgressLayout.visibility=View.VISIBLE
            val filePath = fireseStorage.child(DATA_IMAGES).child(userId!!)
            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener {uri->
                            val url=uri.toString()
                            firebaseDB.collection(DATA_USERS).document(userId!!).update(DATA_USER_IMAGE_URL, url)
                                .addOnSuccessListener {
                                    imageUrl=url
                                    photoIV.loadUrl(imageUrl, R.drawable.logo)
                                }
                            profileProgressLayout.visibility=View.GONE

                        }
                        .addOnFailureListener{
                            onUploadFailure()
                        }
                }
                .addOnFailureListener {
                    onUploadFailure()
                }
        }
    }

    fun onUploadFailure(){
        Toast.makeText(this, "Image upload fail. Please try again later.", Toast.LENGTH_SHORT).show()
        profileProgressLayout.visibility=View.GONE
    }

    fun onSignout(v: View){
        firebaseAuth.signOut()
        finish()
    }

    companion object{
        fun newIntent(context: Context)= Intent (context, ProfileActivity::class.java)
    }
}
