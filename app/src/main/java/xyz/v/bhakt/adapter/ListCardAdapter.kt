package xyz.v.bhakt.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import xyz.v.bhakt.R
import xyz.v.bhakt.models.Song

class ListCardAdapter(val list:ArrayList<Song>) : RecyclerView.Adapter<ListCardAdapter.Mvh>(){

    inner class Mvh(view: View): RecyclerView.ViewHolder(view) {
        val songName:TextView = view.findViewById(R.id.songNameTv)
        val icon: ImageView = view.findViewById(R.id.icon)
        val singerName: TextView = view.findViewById(R.id.singerNameTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Mvh {
        return Mvh(LayoutInflater.from(parent.context).inflate(R.layout.list_card,parent,false))
    }

    override fun onBindViewHolder(holder: Mvh, position: Int) {
        val obj = list[position]
        holder.singerName.text = obj.singerNAme.ifEmpty { "Mixed" }
        holder.songName.text = obj.name
        Glide.with(holder.icon)
            .load(Uri.parse("https://yourimageshare.com/ib/dM62gxmLU0.png"))
            .into(holder.icon)
    }

    override fun getItemCount(): Int {
       return list.size
    }
}