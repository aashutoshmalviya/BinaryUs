package com.example.binaryus.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class HomePostImageModel(
    var userName: String="",
    var timeStamp: String,
    var profileImage:String="",
    var postImage:String="",
    var uid:String="",
    var id:String="",
    var likeCount:String="",
    var comments:String="",
    var description:String=""
)