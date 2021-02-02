package com.example.weather

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.weather.ui.*
import com.example.weatherapp.adapter.TodoAdapter
import com.example.weatherapp.data.Todo
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity(), TodoDialog.TodoHandler {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
                showAddTodoDialog()

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
         //Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
               R.id.nav_home, R.id.todo_dialog_id), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        navView.setNavigationItemSelectedListener { it->

            when (it.itemId) {
                R.id.add_city -> showAddTodoDialog()
                R.id.nav_home -> goHomeFragment()
                R.id.app_info -> showToast()
                else -> { // Note the block
                    showToast()
                }
            }



        }

    }

    private fun goHomeFragment(): Boolean {

        return true

    }

    private fun showToast(): Boolean {

        Toast.makeText(applicationContext,"Moraru Daniela Andreea ELYJ9R",Toast.LENGTH_SHORT)
            .show()
        return true
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)


        return true
    }

    fun showAddTodoDialog(): Boolean {
        TodoDialog().show(supportFragmentManager, "Dialog")
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    override fun todoCreated(todo: Todo) {
        saveTodo(todo)
    }
    private fun saveTodo(todo: Todo) {
        todoAdapter = recyclerTodo.adapter as TodoAdapter
        todoAdapter.addTodo(todo)
    }

    override fun todoUpdated(todo: Todo) {
        TODO("Not yet implemented")
    }
}