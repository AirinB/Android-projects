package com.example.weather.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.weather.MainActivity
import com.example.weather.R
import com.example.weather.WeatherDetails
import com.example.weather.ui.TodoDialog
import com.example.weatherapp.adapter.TodoAdapter
import com.example.weatherapp.data.Todo
import com.example.weatherapp.touch.TodoRecyclerTouchCallback
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.todo_row.*

class HomeFragment : Fragment(), TodoDialog.TodoHandler{

    lateinit var todoAdapter: TodoAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        todoAdapter = TodoAdapter(context as MainActivity)
        recyclerTodo.adapter = todoAdapter

        val touchCallbakList = TodoRecyclerTouchCallback(todoAdapter)
        val itemTouchHelper = ItemTouchHelper(touchCallbakList)
        itemTouchHelper.attachToRecyclerView(recyclerTodo)



    }



    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun todoCreated(todo: Todo) {
        saveTodo(todo)
    }

    private fun saveTodo(todo: Todo) {
        todoAdapter.addTodo(todo)
    }

    override fun todoUpdated(todo: Todo) {
        TODO("Not yet implemented")
    }
}