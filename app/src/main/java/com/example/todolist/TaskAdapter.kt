package com.example.todolist
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onCheckChange: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.titleTxt)
        val editImg: ImageView = itemView.findViewById(R.id.editImg)
        val deleteImg: ImageView = itemView.findViewById(R.id.deleteImg)
        val checkBoxComplete: CheckBox = itemView.findViewById(R.id.checkBoxComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskText.text = task.name


        holder.editImg.setOnClickListener {
            onEditClick(position)
        }

        holder.deleteImg.setOnClickListener {
            onDeleteClick(position)
        }

        holder.checkBoxComplete.setOnCheckedChangeListener(null)
        holder.checkBoxComplete.isChecked = task.isCompleted
        holder.checkBoxComplete.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            onCheckChange(position, isChecked)
            Log.d("TaskAdapter", "Task: ${task.name}, Completed: $isChecked")
        }


    }


    override fun getItemCount(): Int {
        return taskList.size
    }
}
