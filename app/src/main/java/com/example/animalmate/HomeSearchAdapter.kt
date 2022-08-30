package com.example.animalmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeSearchAdapter (var hospital:ArrayList<HospitalInfo>)
    : RecyclerView.Adapter<HomeSearchAdapter.MyViewHolder>(){

    var itemClickListener:OnItemClickListener ?= null
    inner class MyViewHolder(itemView: View)
        :RecyclerView.ViewHolder(itemView){
        var hname : TextView
        var phone :TextView
        var address:TextView
        init{
            hname = itemView.findViewById(R.id.hospitalName)
            phone = itemView.findViewById(R.id.hospitalPhone)
            address = itemView.findViewById(R.id.hospitalAddress)
            itemView.setOnClickListener{
                itemClickListener?.onItemClick(itemView, adapterPosition)
            }
        }
    }
    interface OnItemClickListener{
        fun onItemClick(view : View, position:Int)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_mainmenu, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return hospital.size
    }

    override fun onBindViewHolder(holder: HomeSearchAdapter.MyViewHolder, position: Int) {
        holder.hname.text = hospital[position].hName
        holder.phone.text = hospital[position].hPhone
        holder.address.text = hospital[position].hAddress

    }
}