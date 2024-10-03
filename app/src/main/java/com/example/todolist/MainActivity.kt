package com.example.todolist

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val tasks = mutableListOf<Task>()
    private val completedTasks = mutableListOf<Task>() // Danh sách công việc đã hoàn thành
    private val incompleteTasks = mutableListOf<Task>() // Danh sách công việc chưa hoàn thành
    private var isEditing = false // Biến để kiểm tra xem người dùng đang sửa hay thêm mới
    private var currentEditingPosition: Int = -1 // Vị trí của task đang được sửa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taskInput = findViewById<EditText>(R.id.taskInput)
        val addTaskButton = findViewById<Button>(R.id.addTaskButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // Khởi tạo RecyclerView và Adapter với các callback cho chức năng sửa và xóa
        taskAdapter = TaskAdapter(tasks,
            onEditClick = { position -> showEditConfirmationDialog(position) },
            onDeleteClick = { position -> showDeleteConfirmationDialog(position) },
            onCheckChange = { position, isChecked ->
                tasks[position].isCompleted = isChecked // Cập nhật trạng thái hoàn thành của task

                if (isChecked) {
                    completedTasks.add(tasks[position])
                    incompleteTasks.remove(tasks[position])
                } else {
                    completedTasks.remove(tasks[position])
                    incompleteTasks.add(tasks[position])
                }
                // Cập nhật danh sách hoàn thành và chưa hoàn thành


                taskAdapter.notifyItemChanged(position) // Cập nhật RecyclerView
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        val radioGroupFilter = findViewById<RadioGroup>(R.id.radioGroupFilter)
        radioGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAll -> {
                    Log.d("Filter", "Tất cả công việc được hiển thị")
                    filterTasks("all")
                }
                R.id.radioCompleted -> {
                    Log.d("Filter", "Chỉ công việc hoàn thành được hiển thị")
                    filterTasks("completed")
                }
                R.id.radioIncomplete -> {
                    Log.d("Filter", "Chỉ công việc chưa hoàn thành được hiển thị")
                    filterTasks("incomplete")
                }
                else -> Log.d("Filter", "Không có bộ lọc nào được chọn")
            }
        }


        // Xử lý khi người dùng nhấn nút "Thêm"
        addTaskButton.setOnClickListener {
            val taskName = taskInput.text.toString()
            if (taskName.isNotEmpty()) {
                if (isEditing) {
                    // Nếu đang ở chế độ sửa
                    tasks[currentEditingPosition].name = taskName // Cập nhật task đã chỉnh sửa
                    taskAdapter.notifyItemChanged(currentEditingPosition) // Cập nhật RecyclerView
                    showAlert("Sửa thông tin thành công")
                    isEditing = false // Thoát khỏi chế độ sửa
                    currentEditingPosition = -1 // Đặt lại vị trí chỉnh sửa
                    addTaskButton.text = "+" // Đổi lại nút về trạng thái thêm mới
                } else {

                    val newTask = Task(taskName, false) // Tạo task mới với trạng thái chưa hoàn thành
                    tasks.add(newTask) // Thêm vào danh sách công việc
                    incompleteTasks.add(newTask) // Thêm vào danh sách chưa hoàn thành
                    taskAdapter.notifyItemInserted(tasks.size - 1) // Cập nhật RecyclerView

                }
                taskInput.text.clear() // Xóa nội dung trong EditText sau khi thêm hoặc sửa
            } else {
                showAlert("Vui lòng nhập nội dung công việc")
            }
        }
    }

    private fun filterTasks(filter: String) {
        Log.d("FilterTasks", "Bắt đầu lọc với bộ lọc: $filter")
        tasks.clear()
        when (filter) {
            "all" -> tasks.addAll(completedTasks + incompleteTasks)
            "completed" -> tasks.addAll(completedTasks)
            "incomplete" -> tasks.addAll(incompleteTasks)
        }
        Log.d("FilterTasks", "Số lượng công việc sau khi lọc: ${tasks.size}")
        taskAdapter.notifyDataSetChanged()
    }



    // Hiển thị hộp thoại xác nhận trước khi sửa
    private fun showEditConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận sửa")
        builder.setMessage("Bạn có muốn sửa công việc này không?")
        builder.setPositiveButton("Có") { _, _ -> editTask(position) }
        builder.setNegativeButton("Không") { dialog, _ -> dialog.dismiss() } // Đóng hộp thoại nếu không muốn sửa
        val dialog = builder.create()
        dialog.show()
    }

    // Hiển thị hộp thoại xác nhận trước khi xóa
    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận xóa")
        builder.setMessage("Bạn có chắc chắn muốn xóa công việc này không?")
        builder.setPositiveButton("Có") { _, _ -> deleteTask(position) }
        builder.setNegativeButton("Không") { dialog, _ -> dialog.dismiss() } // Đóng hộp thoại nếu không muốn xóa
        val dialog = builder.create()
        dialog.show()
    }

    // Xử lý sửa công việc
    private fun editTask(position: Int) {
        val taskInput = findViewById<EditText>(R.id.taskInput)
        taskInput.setText(tasks[position].name) // Hiển thị nội dung công việc trong EditText
        currentEditingPosition = position // Cập nhật vị trí của task đang sửa
        isEditing = true // Kích hoạt chế độ sửa
        findViewById<Button>(R.id.addTaskButton).text = "OK" // Đổi nhãn nút thành "Sửa"
    }

    // Xử lý xóa công việc
    private fun deleteTask(position: Int) {
        val task = tasks[position]
        tasks.removeAt(position) // Xóa công việc khỏi danh sách
        if (task.isCompleted) {
            completedTasks.remove(task) // Xóa khỏi danh sách đã hoàn thành
        } else {
            incompleteTasks.remove(task) // Xóa khỏi danh sách chưa hoàn thành
        }
        taskAdapter.notifyItemRemoved(position) // Cập nhật RecyclerView
        showAlert("Xoá thành công!")
    }
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thông báo")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }
}
