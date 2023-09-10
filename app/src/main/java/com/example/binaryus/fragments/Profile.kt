package com.example.binaryus.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binaryus.R
import com.example.binaryus.model.PostImageModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import de.hdodenhof.circleimageview.CircleImageView


class Profile : Fragment() {

    private lateinit var nameprofile: TextView
    private lateinit var statusprofile: TextView
    private lateinit var followingprofile: TextView
    private lateinit var followerprofile: TextView
    private lateinit var postprofile: TextView
    private lateinit var profileimage_profile: CircleImageView
    private lateinit var recyclerView_profile: RecyclerView
    private lateinit var user: FirebaseUser
    private lateinit var followbtn_profile: Button
    private var isMyProfile: Boolean = true
    private lateinit var uid:String
    private lateinit var adapter: FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>
    private lateinit var changeprofile:ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniit(view)
        if (isMyProfile) followbtn_profile.visibility = VISIBLE
        else followbtn_profile.visibility = VISIBLE
        loadBasicData()
        recyclerView_profile.setHasFixedSize(true)
        recyclerView_profile.layoutManager=GridLayoutManager(context,3)
        loadPostImages()
        recyclerView_profile.adapter=adapter
        onclick()
    }

    //Todo in video 15
    private fun onclick() {
        changeprofile.setOnClickListener {
            Toast.makeText(context,"CLICKED",Toast.LENGTH_LONG).show()
        }
    }

    private fun loadPostImages() {
        if (isMyProfile)uid= user.uid

        val reference: DocumentReference =
            FirebaseFirestore.getInstance().collection("Users").document(uid)
        val query: Query = reference.collection("Posts")
        val options: FirestoreRecyclerOptions<PostImageModel> =
            FirestoreRecyclerOptions.Builder<PostImageModel>()
                .setQuery(query, PostImageModel::class.java)
                .build()

        class PostAdapter : FirestoreRecyclerAdapter<PostImageModel, PostImageHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageHolder {
                val view=LayoutInflater.from(parent.context).inflate(R.layout.profile_image_items,parent,false)
                return PostImageHolder(view)
            }

            override fun onBindViewHolder(
                holder: PostImageHolder,
                position: Int,
                model: PostImageModel
            ) {

                Glide.with(holder.imageView.context.applicationContext)
                    .load(model.postImage)
                    .into(holder.imageView)
            }

        }

        adapter=PostAdapter()
    }

    class PostImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profileItem_imageView)

    }

    private fun iniit(view: View) {
        nameprofile = view.findViewById(R.id.nameprofile)
        statusprofile = view.findViewById(R.id.aboutprofile)
        followingprofile = view.findViewById(R.id.followindprofile)
        followerprofile = view.findViewById(R.id.followerprofile)
        postprofile = view.findViewById(R.id.postcntprofile)
        profileimage_profile = view.findViewById(R.id.profilepic_profile)
        followbtn_profile = view.findViewById(R.id.followbtn_profile)
        recyclerView_profile = view.findViewById(R.id.recyclerviewprofile)
        changeprofile=view.findViewById(R.id.change_profile)
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

    }

    private fun loadBasicData() {
        val userRef: DocumentReference = FirebaseFirestore.getInstance().collection("Users")
            .document(user.uid)
        userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("error", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                nameprofile.text = snapshot.getString("name")
                statusprofile.text = snapshot.getString("status")
                followerprofile.text = snapshot.getString("follower")
                followingprofile.text = snapshot.getString("following")
                postprofile.text = snapshot.getString("posts")
                context?.let {
                    Glide.with(it.applicationContext).load(snapshot.getString("profileImage"))
                        .into(profileimage_profile)
                }
            } else {
                Log.d("error", "Current data: null")
            }
        }
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}