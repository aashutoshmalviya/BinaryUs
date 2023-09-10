package com.example.binaryus.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binaryus.Adapters.HomeAdapter
import com.example.binaryus.R
import com.example.binaryus.model.HomePostImageModel
import com.example.binaryus.model.PostImageModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Home : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: FirebaseUser
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniit(view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadDataFromFireStore()


//        documentReference=FirebaseFirestore.getInstance().collection("Posts").document(user.uid)
//        homeAdapter= HomeAdapter(list,context)
//        recyclerView.adapter=homeAdapter
    }

    private fun loadDataFromFireStore() {

        var list = mutableListOf<HomePostImageModel>()
        var uidlist = mutableListOf<String>()

        val db = Firebase.firestore

//        db.collection("Users")
//            .get()
//            .addOnSuccessListener { result ->
//                for (doc in result) {
                    db.collection("Users").document(user.uid)
                        .collection("Posts")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                list.add(
                                    HomePostImageModel(
                                        document.data.get("userName").toString(),
                                        "",
                                        document.data.get("profileImage").toString(),
                                        document.data.get("postImage").toString(),
                                        document.data.get("uid").toString(),
                                        document.data.get("id").toString(),
                                        document.data.get("likeCount").toString(),
                                        document.data.get("comments").toString(),
                                        document.data.get("description").toString()
                                    )
                                )
                                Log.d("data", "${document.id} => ${document.data}")
                            }
                            for (item in list)
                            Log.d("Size", item.postImage)
                            adapter = HomeAdapter(list)
                            recyclerView.adapter = adapter
                        }.addOnFailureListener { exception ->
                            Log.d("data", "Error getting documents: ", exception)
                        }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.d("data", "Error getting users: ", exception)
//            }.addOnCompleteListener{
//            }


        /*class HomePostAdapter : FirestoreRecyclerAdapter<PostImageModel, HomePostImageHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostImageHolder {
                val view=LayoutInflater.from(parent.context).inflate(R.layout.post_layout,parent,false)
                return HomePostImageHolder(view)
            }

            override fun onBindViewHolder(
                holder: HomePostImageHolder,
                position: Int,
                model: PostImageModel
            ) {
                holder.name.text=model.userName
                holder.time.text=model.timeStamp
                holder.likecount.text=model.likeCount
                Glide.with(holder.imageView.context.applicationContext)
                    .load(model.postImage)
                    .into(holder.imageView)
            }

        }

        adapter=HomePostAdapter()

        val reference:DocumentReference= FirebaseFirestore.getInstance().collection("Posts").document(user.uid)

        list.add(HomeModel("Ashutosh","01/11/2020","","https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/640px-Image_created_with_a_mobile_phone.png","123","12","121","kaise ho bhai","saab badhiya"))
        list.add(HomeModel("Ashutosh","02/11/2020","","https://rsjlawang.com/assets/images/lightbox/image-3.jpg","1234","19","121","kaise ho bhai","saab badhiya"))
        Log.d("size",list.size.toString())
        homeAdapter.notifyDataSetChanged()*/
    }



    private fun iniit(view: View) {
        recyclerView = view.findViewById(R.id.recyclerviewpost)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser!!
    }
}

