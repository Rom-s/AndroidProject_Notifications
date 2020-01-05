package com.example.td2


import android.R.attr.name
import android.app.*
import android.content.*
import android.content.Context.ALARM_SERVICE
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
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
                for (t in it) {
                    setAlarmManager(t)
                }
                adapter.notifyDataSetChanged()
            }
        })
    }

    private val coroutineScope = MainScope()

    fun setAlarmManager(task : Task){
        var cal = Calendar.getInstance()
        /*cal.set(Calendar.YEAR, 2020)
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DAY_OF_MONTH, 4)
        cal.set(Calendar.HOUR_OF_DAY, 15)
        cal.set(Calendar.MINUTE, 8)
        cal.set(Calendar.SECOND,0)*/
        val myFormat = "MM/dd/yyyy-HH:mm"//format de la date et de l'heure. L'heure est fixée dans le dialogue de l'heure.
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        cal.setTime(sdf.parse(task.description.lines()[0])!!)
        cal.add(Calendar.MINUTE,-5)
        cal.set(Calendar.SECOND,0)
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, Receiver::class.java)
        intent.putExtra("text", task.title)
        intent.putExtra("id", task.id)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        Log.d("MainActivity", " Create : " + Date().toString() + "cal date :" + sdf.format(cal.getTime()))
        //la ligne ci-dessous envoie une alamre (execute la fonction receive) à la date de cal
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
        //envoie une alarme 10 secondes après l'appel de setAlarmManager
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent)
    }

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
                setAlarmManager(task)
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun removeTask(id : String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tasks.removeIf { t -> t.id == id }
        }
        else {
            val task = tasks.find { t -> t.id == id }
            tasks.remove(task)
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

        //Crée le dialogue de l'heure, c'est lui qui va lancer le dialogue de la date.
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            val editTitle = dialog.findViewById(R.id.task_title) as EditText
            val editDescription = dialog.findViewById(R.id.task_description) as EditText
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val myFormat = "MM/dd/yyyy-HH:mm"//format de la date et de l'heure. L'heure est fixée dans le dialogue de l'heure.
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            createTask(editTitle.text.toString(), sdf.format(cal.getTime()) + "\n" + editDescription.text.toString())

        }

        //crée le dialogue demandant la date. C'est dedans que je crée la tache, étant donné que c'est le ernier dialogue à s'afficher
        val dpd = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            TimePickerDialog(context!!, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }, year, month, day)



        //lance le dialogue de l'heure à la fin du dialogue de la tache.
        buttonCreate.setOnClickListener {
            dialog.dismiss()
            dpd.show()


        }

        val buttonCancel = dialog.findViewById(R.id.button_cancel) as Button
        buttonCancel.setOnClickListener{ dialog.cancel() }

        dialog.show()
    }

    class Receiver : BroadcastReceiver() {
        //test de notification qui marche
        fun createNotificationChannel(context: Context, channelId : String,  name: String, descriptionText: String) {
            // 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(channel)
            }
        }
        //fonction qui est déclenchée par l'alarme, c'est ce qui va être lacé 5 minutes avant l'échéance de la tache
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MainActivity", " Receiver : " + Date().toString())

            val channelId = "task_channel"
            val notifId = intent.hashCode()

            val returnIntent = Intent(context, MainActivity::class.java)
            returnIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, returnIntent, 0)


            // "Mark as done" action
            val buttonIntent = Intent(context, ActionReceiver::class.java).apply {
                action = "Mark the task as done."
            }
            buttonIntent.putExtra("taskId", intent?.getStringExtra("id"))
            buttonIntent.putExtra("notifId", notifId)
            val buttonPendingIntent : PendingIntent = PendingIntent.getBroadcast(context, 0, buttonIntent, 0)
            val action: NotificationCompat.Action = NotificationCompat.Action.Builder(R.drawable.ic_check_black_24dp,"MARK AS DONE", buttonPendingIntent).build()

            createNotificationChannel(context!!, channelId, "Task Channel","Task Channel")

            val mBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("The due date of a task is approaching.")
                .setContentText(intent?.getStringExtra("text"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(action)
                .setAutoCancel(true)
                .build()

            with(NotificationManagerCompat.from(context)) {
                notify(notifId, mBuilder)
            }
        }
    }

    inner class ActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("MainActivity", "toto")
            val id = intent?.getStringExtra("taskId")!!
            Log.d("MainActivity", "Remove Receiver : $id")
            coroutineScope.launch {
                Api.INSTANCE.tasksService.deleteTask(id)
            }
            removeTask(id)
            adapter.notifyDataSetChanged()

            with(NotificationManagerCompat.from(context!!)) {
                cancel(intent.getIntExtra("notifId", 0))
            }

            val mainActivity = Intent(activity, MainActivity::class.java)
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(mainActivity)
        }
    }
}