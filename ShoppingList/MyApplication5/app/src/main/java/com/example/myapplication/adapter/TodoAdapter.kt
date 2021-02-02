package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ScrollingActivity
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Todo
import com.example.myapplication.touch.TodoTouchHelperCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.todo_row.view.*
import java.util.*
import kotlinx.android.synthetic.main.activity_scrolling.*

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>,
    TodoTouchHelperCallback {

    var todoItems = mutableListOf<Todo>()
    val context: Context

    constructor(context: Context, listTodos: List<Todo>) {
        this.context = context
        todoItems.addAll(listTodos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.todo_row, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentTodo = todoItems[position]

        holder.tvDate.text = currentTodo.createDate
        holder.cbDone.text = currentTodo.todoText
        holder.cbDone.isChecked = currentTodo.done
        holder.tvDescription.text = currentTodo.Description
        holder.tvPrice.text = currentTodo.price


        holder.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditTodoDialog(
                    todoItems[holder.adapterPosition], holder.adapterPosition
            )
        }

        holder.cbDone.setOnClickListener {
            todoItems[holder.adapterPosition].done = holder.cbDone.isChecked
            Thread{
                var database =  AppDatabase.getInstance(context).todoDao()
                database.updateTodo(todoItems[holder.adapterPosition])
                var todoItems = database.getAllTodos()
                (context as ScrollingActivity).runOnUiThread {
                    context.setStats(todoItems)
                }

            }.start()

        }

        if (todoItems[holder.adapterPosition].category == 0) {
            holder.ivIcon.setImageResource(R.drawable.fruits)
        } else if (todoItems[holder.adapterPosition].category == 1) {
            holder.ivIcon.setImageResource(R.drawable.vegies)
        } else if (todoItems[holder.adapterPosition].category == 2) {
            holder.ivIcon.setImageResource(R.drawable.milk)
        } else if (todoItems[holder.adapterPosition].category == 3) {
            holder.ivIcon.setImageResource(R.drawable.pills)
        } else if (todoItems[holder.adapterPosition].category == 4) {
            holder.ivIcon.setImageResource(R.drawable.tea)
        } else if (todoItems[holder.adapterPosition].category == 5) {
            holder.ivIcon.setImageResource(R.drawable.care)
        }
    }




    private fun deleteTodo(position: Int) {
        Thread{
            AppDatabase.getInstance(context).todoDao().deleteTodo(
                    todoItems[position])

            (context as ScrollingActivity).runOnUiThread {
                todoItems.removeAt(position)
                notifyItemRemoved(position)
            }


        }.start()
    }

    public fun addTodo(todo: Todo) {
        todoItems.add(todo)

        //notifyDataSetChanged() // this refreshes the whole list
        notifyItemInserted(todoItems.lastIndex)
    }

    public fun updateTodo(todo: Todo, editIndex: Int) {
        todoItems[editIndex] = todo

        notifyItemChanged(editIndex)
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate = itemView.tvDate
        val cbDone = itemView.cbDone
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        val ivIcon = itemView.ivIcon
        val tvDescription = itemView.tvDescription
        val tvPrice = itemView.tvPrice


    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(todoItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }
}