package com.domain.posthere

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListaPostAdapter() : RecyclerView.Adapter<ListaPostAdapter.ViewHolder>() {

    var listaPosts = ArrayList<Post>()
        set(value) {
            field = value
            Log.i("DR3", "listaPosts.set ---> ${value.size}")
            notifyDataSetChanged()
        }

    lateinit var itemListener : RecycleViewItemListener
    fun setRecyclerViewItemListener(listener: MainActivity) {
        itemListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_list, parent, false)
        Log.i("DR3", "listaPosts.onCreateViewHolder ---> ${listaPosts.size}")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("DR3", "listaPosts.onBindViewHolder ---> ${listaPosts.size}")
        holder.bindItem(listaPosts[position], itemListener, position)
    }

    override fun getItemCount(): Int {
        Log.i("DR3", "listaPosts.getItemCount ---> ${listaPosts.size}")
        return listaPosts.size
    }

    //Classe Interna = relação Tdo - Parte
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(post : Post, itemListener: RecycleViewItemListener, position: Int) {

            val rowNome = itemView.findViewById<TextView>(R.id.rowUser)
            rowNome.text = post.user
            val rowEmail = itemView.findViewById<TextView>(R.id.rowDate)
            rowEmail.text = post.date
            val rowFone = itemView.findViewById<TextView>(R.id.rowContent)
            rowFone.text = post.content
            //--------------------------------------------------------------------------------------
            itemView.setOnClickListener {
                itemListener.recycleViewItemClicked(it, post.id!!)
            }
        }
    }
}