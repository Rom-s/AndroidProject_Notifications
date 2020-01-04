package com.example.td2


import android.R.attr.name
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ClipDescription
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.td2.network.Api
import com.example.td2.network.TasksRepository
import kotlinx.android.synthetic.main.tasks_fragment.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


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
        updateTasks()
        val view = inflater.inflate(R.layout.tasks_fragment, container, false)
        view.tasks_recycler_view.adapter = adapter
        view.tasks_recycler_view.layoutManager = LinearLayoutManager(context)
        view.create_button.setOnClickListener{ createDialog() }
        return view
    }

    fun updateTasks() {
        tasksRepository.getTasks().observe(this, Observer {
            if (it != null) {
                tasks.clear()
                tasks.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private val coroutineScope = MainScope()

    fun createTask(title: String, description: String) {
        val task = Task("null", title, description)
        coroutineScope.launch {
            val fetchedTask = Api.INSTANCE.tasksService.createTask(task)
            if (!fetchedTask.isSuccessful)
            {
                Toast.makeText(context, "An error occurred. Try again.", Toast.LENGTH_LONG).show()
            }
            else {
                tasks.add(fetchedTask.body()!!)
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun createDialog() {
        var cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dialog = Dialog(context!!)
        dialog.setContentView(R.layout.dialog_create_task)

        val buttonCreate = dialog.findViewById(R.id.button_ok) as Button

        //crée le dialogue demandant la date. C'est dedans que je crée la tache, étant donné que c'est le ernier dialogue à s'afficher
        val dpd = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val editTitle = dialog.findViewById(R.id.task_title) as EditText
            val editDescription = dialog.findViewById(R.id.task_description) as EditText
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "MM/dd/yyyy-HH:mm"//format de la date et de l'heure. L'heure est fixée dans le dialogue de l'heure.
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            createTask(editTitle.text.toString(), sdf.format(cal.getTime()) + "\n" + editDescription.text.toString())
        }, year, month, day)

        //Crée le dialogue de l'heure, c'est lui qui va lancer le dialogue de la date.
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            dpd.show()
        }

        //lance le dialogue de l'heure à la fin du dialogue de la tache.
        buttonCreate.setOnClickListener {
            dialog.dismiss()
            TimePickerDialog(context!!, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

        }

        val buttonCancel = dialog.findViewById(R.id.button_cancel) as Button
        buttonCancel.setOnClickListener{ dialog.cancel() }

        dialog.show()
    }
}