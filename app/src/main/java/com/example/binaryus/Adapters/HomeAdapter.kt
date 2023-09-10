package com.example.binaryus.Adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binaryus.R
import com.example.binaryus.model.HomePostImageModel

class HomeAdapter(var list: MutableList<HomePostImageModel>): RecyclerView.Adapter<HomeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.post_layout,parent,false);
        return  HomeHolder(view);
    }

    override fun getItemCount(): Int {
        return list.size;
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        holder.username.text=list.get(position).userName
        holder.time.text=list.get(position).timeStamp
        Glide.with(holder.itemView.context.applicationContext)
            .load(list.get(position).postImage)
            .into(holder.postPic)
        Glide.with(holder.itemView.context.applicationContext)
            .load(list.get(position).profileImage)
            .into(holder.profilePic)
        holder.likeCount.text=list.get(position).likeCount

    }

}
class HomeHolder(itemView: View):RecyclerView.ViewHolder(itemView){
    val username:TextView=itemView.findViewById(R.id.namepost)
    val time:TextView=itemView.findViewById(R.id.timepost)
    val profilePic:ImageView=itemView.findViewById(R.id.profilepicpost)
    val postPic:ImageView=itemView.findViewById(R.id.post_pic)
    val likeCount:TextView=itemView.findViewById(R.id.likecount)
}