package com.stevennt.movemate.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stevennt.movemate.R
import com.stevennt.movemate.data.model.Workouts
import com.stevennt.movemate.ui.detail.DetailActivity

class ListWorkoutAdapter(private var listWorkout: List<Workouts>) : RecyclerView.Adapter<ListWorkoutAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Workouts>) {
        listWorkout = newList
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Workouts)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_workouts, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val workout = listWorkout[position]
        holder.bind(workout)

        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
            intentDetail.putExtra("key_workouts", listWorkout[holder.adapterPosition])
            holder.itemView.context.startActivity(intentDetail)
        }
    }

    override fun getItemCount(): Int = listWorkout.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPhoto: ImageView = itemView.findViewById(R.id.iv_workout)
        private val tvName: TextView = itemView.findViewById(R.id.tv_workout_name)
        private val tvReps: TextView = itemView.findViewById(R.id.tv_reps)

        fun bind(workout: Workouts) {
            imgPhoto.setImageResource(workout.icon)
            tvName.text = workout.name
            tvReps.text = workout.reps
        }
    }
}
