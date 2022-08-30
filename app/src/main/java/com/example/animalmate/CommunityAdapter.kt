package com.example.animalmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class CommunityAdapter(options: FirebaseRecyclerOptions<CommunityInfo>)
    : FirebaseRecyclerAdapter<CommunityInfo, CommunityAdapter.ViewHolder>(
    options
) {
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var communityid : TextView
        var communitytitle : TextView
        var communitycontent : TextView
        init{
            communityid = itemView.findViewById(R.id.community_ID)
            communitytitle = itemView.findViewById(R.id.community_title)
            communitycontent = itemView.findViewById(R.id.community_content)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_community, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: CommunityInfo) {
        holder.communityid.text = model.id
        holder.communitytitle.text = model.title
        holder.communitycontent.text = model.content
    }
}
