package com.example.binaryus.fragments

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binaryus.Adapters.GalleryAdapter
import com.example.binaryus.R
import com.example.binaryus.model.GalleryImageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Add : Fragment() {
    private lateinit var descriptionEditText: EditText
    private lateinit var imageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var nextButton: Button
    private lateinit var postButton: Button
    private lateinit var addLayout: RelativeLayout
    private lateinit var linearLayout: LinearLayout
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var scrollView: ScrollView
    private lateinit var imageUri: Uri
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var progressBar: RelativeLayout
//    private lateinit var logout: ImageButton
//    private lateinit var toolbar_tv: TextView


    private var list = mutableListOf<GalleryImageModel>()
    private var imageUrl: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        super.onViewCreated(view, savedInstanceState)


        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.setHasFixedSize(true)
        list = mutableListOf()
        galleryAdapter = GalleryAdapter(list)
        recyclerView.adapter = galleryAdapter
        clicklistener()

    }

    private fun clicklistener() {
        galleryAdapter.SendImage(object : GalleryAdapter.SendImage {
            override fun onSend(picUri: Uri) {
                addLayout.visibility = VISIBLE
                nextButton.visibility = VISIBLE
                imageUri=picUri
                context?.let {
                    Glide.with(it)
                        .load(picUri)
                        .into(imageView)
                }
            }

        })

        nextButton.setOnClickListener({
            descriptionEditText.visibility = VISIBLE
            nextButton.visibility = GONE
            recyclerView.visibility = GONE
            linearLayout.visibility = VISIBLE
            scrollView.visibility = GONE
        })
        backButton.setOnClickListener({
            descriptionEditText.visibility = GONE
            nextButton.visibility = VISIBLE
            recyclerView.visibility = VISIBLE
            linearLayout.visibility = GONE
            scrollView.visibility = VISIBLE
        })
        postButton.setOnClickListener(View.OnClickListener {
            progressBar.visibility = VISIBLE
            imageView.visibility = GONE
            descriptionEditText.visibility = GONE
            linearLayout.visibility = GONE
            var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
            var storageReference =
                firebaseStorage.reference.child("Post Images" + System.currentTimeMillis())
            storageReference.putFile(imageUri)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            imageUrl = uri.toString()
                            uploadData(imageUrl)
                        }
                    }

                })

        })


    }

    private fun uploadData(imageUrl: String) {
        var collectionReference = FirebaseFirestore.getInstance().collection("Users")
            .document(firebaseUser.uid).collection("Posts")

        var map = HashMap<String, String>()

        map.put("userName",firebaseUser.displayName+"")

//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//        val current = LocalDateTime.now().format(formatter)
//        map.put("timeStamp",current);
        map.put("timeStamp", FieldValue.serverTimestamp().toString())

        map.put("profileImage",""+firebaseUser.photoUrl)
        map.put("postImage", imageUrl)
        map.put("uid",firebaseUser.uid)

        var sdf = SimpleDateFormat("ddmmyyyyhhmmss")
        var currentDate = sdf.format(Date())
        map.put("id", currentDate)

        map.put("likeCount","")
        map.put("comments","")

        map.put("description", descriptionEditText.text.toString())

        collectionReference.document(currentDate).set(map)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    Toast.makeText(
                        context,
                        "Sucessfully Uploaded",

                        Toast.LENGTH_SHORT
                    ).show()
                else Toast.makeText(
                    context,
                    "Error" + task.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                activity?.recreate()
            }
    }

    private fun init(view: View) {
        descriptionEditText = view.findViewById(R.id.add_description)
        imageView = view.findViewById(R.id.add_image_view)
        recyclerView = view.findViewById(R.id.add_recylerview)
        backButton = view.findViewById(R.id.add_backbutton)
        nextButton = view.findViewById(R.id.add_nextbutton)
        postButton = view.findViewById(R.id.add_postbutton)
        linearLayout = view.findViewById(R.id.add_linearlayout)
        addLayout = view.findViewById(R.id.add_layout)
        scrollView = view.findViewById(R.id.add_scrollview)
        progressBar = view.findViewById(R.id.add_progress)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!



    }

    override fun onResume() {
        super.onResume()

        activity?.runOnUiThread(Runnable {
            Dexter.withActivity(context as Activity?)
                .withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        if (p0 != null) if (p0.areAllPermissionsGranted()) {
                            val file = File(
                                Environment.getExternalStorageDirectory()
                                    .toString() + "/Download"
                            )
                            if (file.exists()) {
                                val files = file.listFiles()
                                for (items in files) {
                                    if (items.absolutePath.endsWith(".jpg") || items.absolutePath.endsWith(
                                            ".png"
                                        )
                                    )
                                        list.add(GalleryImageModel(Uri.fromFile(items)))
                                    galleryAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        TODO("Not yet implemented")
                    }

                })
                .onSameThread().check()

        })
    }

}