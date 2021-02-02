package com.example.myapplication

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.myapplication.adapter.TodoAdapter
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Todo
import com.example.myapplication.touch.TodoRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*

// TODO
//  1. icon
//  2. style the row
//

class ScrollingActivity : AppCompatActivity(), TodoDialog.TodoHandler{

    lateinit var todoAdapter: TodoAdapter
    companion object {
        const val KEY_EDIT = "KEY_EDIT"

        const val PREF_NAME = "PREFTODO"
        const val KEY_STARTED = "KEY_STARTED"
        const val KEY_LAST_USED = "KEY_LAST_USED"
    }
    var editIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout).title = title
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            showAddTodoDialog()
        }

        Thread {
            var todoList = AppDatabase.getInstance(this).todoDao().getAllTodos()

            runOnUiThread{
                todoAdapter = TodoAdapter(this, todoList)
                recyclerTodo.adapter = todoAdapter

                val touchCallbakList = TodoRecyclerTouchCallback(todoAdapter)
                val itemTouchHelper = ItemTouchHelper(touchCallbakList)
                itemTouchHelper.attachToRecyclerView(recyclerTodo)

                setStats(todoList)
            }
        }.start()

    }

    fun setStats(todoList: List<Todo>) {

        var (itemsLeft, totalPriceLeft, totalSpent) = getStats(todoList)
        tvItemsLeft.text = itemsLeft.toString()
        tvPriceLeft.text = totalPriceLeft.toString()
        tvTotalSpent.text = totalSpent.toString()
    }

    fun showAddTodoDialog() {
        TodoDialog().show(supportFragmentManager, "Dialog")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
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

        //setStats(todoList)
    }

    private fun saveTodo(todo: Todo) {
        Thread{
            var database = AppDatabase.getInstance(this).todoDao()
            database.insertTodo(todo)
            var todoItems = database.getAllTodos()

            runOnUiThread {
                todoAdapter.addTodo(todo)
                setStats(todoItems)
            }
        }.start()
    }

    override fun todoUpdated(todo: Todo) {
        Thread{
            var database = AppDatabase.getInstance(this).todoDao()
            database.updateTodo(todo)
            var todoItems = database.getAllTodos()

            runOnUiThread {
                todoAdapter.updateTodo(todo, editIndex)
                setStats(todoItems)
            }
        }.start()
    }


    public fun getStats(todoList: List<Todo>): Triple<Int, Int, Int> {
        var totalSpent = 0
        var totalLeft = 0
        var totalPriceLeft = 0
        if (todoList.isEmpty()) return Triple(0, 0, 0);
        for(todo in todoList){
            if (!todo.done){
                totalLeft += 1
                totalPriceLeft += if(todo.price == "") 0 else todo.price.toInt()
            }else{
                totalSpent += if(todo.price == "") 0 else todo.price.toInt()
            }
        }
        return Triple(totalLeft, totalPriceLeft, totalSpent)

    }
    public fun showEditTodoDialog(todoToEdit: Todo, index: Int) {
        editIndex = index

        val editItemDialog = TodoDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_EDIT, todoToEdit)
        editItemDialog.arguments = bundle

        editItemDialog.show(supportFragmentManager, "EDITDIALOG")
    }
}
