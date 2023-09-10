package com.example.binaryus.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.binaryus.R
import com.example.binaryus.model.GalleryImageModel

class GalleryAdapter(var list: MutableList<GalleryImageModel>) : RecyclerView.Adapter<GalleryAdapter.GalleryHolder>() {

    lateinit var  onSendImage: SendImage
    class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val imageView:ImageView=itemView.findViewById(R.id.additem_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_image_item, parent, false)
        return GalleryHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        Glide.with(holder.itemView.context.applicationContext)
            .load(list.get(position).picUri)
            .into(holder.imageView)
        holder.imageView.setOnClickListener(View.OnClickListener {
            chooseImage(list.get(position).picUri)
        })
    }

    private fun chooseImage(picUri: Uri?) {
        if (picUri != null) {
            onSendImage.onSend(picUri)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface SendImage{
        fun onSend(picUri: Uri);
    }
    fun SendImage(sendImage: SendImage){
        onSendImage= sendImage
    }
}