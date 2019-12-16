package com.example.td2


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.td2.network.TasksRepository
import kotlinx.android.synthetic.main.tasks_fragment.view.*

class TasksFragment : Fragment() {

    private val tasksRepository = TasksRepository()
//    private val tasks = arrayOf(
//        Task(id = "id_1", title = "Task 1", description = "description 1"),
//        Task(id = "id_2", title = "Task 2", description = "description 1"),
//        Task(id = "id_3", title = "Task 3", description = "description 1")
//    ).toMutableList()
    private val tasks = mutableListOf<Task>()
    private val adapter = TasksAdapter(tasks)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tasksRepository.getTasks().observe(this, Observer {
            if (it != null) {
                tasks.clear()
                tasks.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
        val view = inflater.inflate(R.layout.tasks_fragment, container, false)
        view.tasks_recycler_view.adapter = adapter
        view.tasks_recycler_view.layoutManager = LinearLayoutManager(context)
        return view
    }
}