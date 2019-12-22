package com.example.td2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.td2.network.Api
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TasksAdapter(private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
        holder.itemView.button_delete.setOnClickListener { onDeleteClickListener(tasks[position]) }
    }

    private val coroutineScope = MainScope()

    private fun onDeleteClickListener(task : Task) {
        coroutineScope.launch {
            Api.INSTANCE.tasksService.deleteTask(task.id)
        }
        tasks.remove(task)
        notifyDataSetChanged()
    }
}