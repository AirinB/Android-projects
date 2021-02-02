package com.example.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.MainActivity
import com.example.weather.R
import com.example.weather.WeatherDetails
import com.example.weather.home.HomeFragment
import com.example.weatherapp.data.Todo
import com.example.weatherapp.touch.TodoTouchHelperCallback
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.android.synthetic.main.todo_row.view.*
import java.util.*


class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>, TodoTouchHelperCallback{

    var todoItems = mutableListOf<Todo>(
            Todo(null, "", "London"),
            Todo(null, "", "Budapest"),
            Todo(null, "", "Berlin")
    )
    val context: Context

    constructor(context: Context) {
        this.context = context
        //todoItems.addAll(listTodos)
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

        holder.tvCity.text = currentTodo.todoText
        holder.btnDelete.setOnClickListener {
            deleteTodo(holder.adapterPosition)
        }

        holder.tvCity.setOnClickListener {

            val fragmentB = WeatherDetails.newInstance(holder.tvCity.text.toString())
            (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragmentB, "fragmnetId")
                    .addToBackStack(null)
                    .commit();



        }

    }

    private fun deleteTodo(position: Int) {
        todoItems.removeAt(position)
        notifyItemRemoved(position)
    }

    public fun addTodo(todo: Todo) {
        todoItems.add(todo)

        //notifyDataSetChanged() // this refreshes the whole list
        notifyItemInserted(todoItems.lastIndex)
    }

    public fun updateTodo(todo: Todo, editIndex: Int) {
        todoItems.set(editIndex, todo)
        notifyItemChanged(editIndex)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnDelete = itemView.btnDelete
        val ivIcon = itemView.ivIcon
        val tvCity = itemView.tvCity


    }

    override fun onDismissed(position: Int) {
        deleteTodo(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(todoItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }



}