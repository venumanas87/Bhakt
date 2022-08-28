package xyz.v.bhakt.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import xyz.v.bhakt.R
import xyz.v.bhakt.SongListActivity
import xyz.v.bhakt.models.HomeRc

class RcHomeAdapter(val rcList:ArrayList<HomeRc>): RecyclerView.Adapter<RcHomeAdapter.mvh>() {

    inner class mvh(view:View): RecyclerView.ViewHolder(view) {

        val mainCard:RelativeLayout = view.findViewById(R.id.round_card)
        val icon:ImageView = view.findViewById(R.id.icon)
        val title:TextView = view.findViewById(R.id.textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mvh {
        return mvh(LayoutInflater.from(parent.context).inflate(R.layout.rc_home_card,parent,false))
    }

    override fun onBindViewHolder(holder: mvh, position: Int) {
        val obj = rcList[position]
        Glide.with(holder.icon)
            .load(obj.imgLink)
            .into(holder.icon)
        holder.title.text = obj.title
        holder.mainCard.setOnClickListener { view ->
            view.context.let {
                it.startActivity(Intent(it,SongListActivity::class.java))
            }
        }
    }

    override fun getItemCount(): Int {
        return  rcList.size
    }
}