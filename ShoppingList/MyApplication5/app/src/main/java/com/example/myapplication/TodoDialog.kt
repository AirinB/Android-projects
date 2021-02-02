package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myapplication.data.Todo
import kotlinx.android.synthetic.main.todo_dialog.view.*
import java.util.*


class TodoDialog : DialogFragment() {

    interface TodoHandler{
        fun todoCreated(todo: Todo)
        fun todoUpdated(todo: Todo)
    }

    lateinit var todoHandler: TodoHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is TodoHandler){
            todoHandler = context
        } else {
            throw RuntimeException(
                "The Activity is not implementing the TodoHandler interface.")
        }
    }

    lateinit var etTodoText: EditText
    lateinit var cbTodoDone: CheckBox
    lateinit var spinnerCategory: Spinner
    lateinit var etDescription: EditText
    lateinit var etPrice: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle("Todo dialog")
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.todo_dialog, null
        )

        etTodoText = dialogView.etTodoText
        cbTodoDone = dialogView.cbTodoDone
        spinnerCategory = dialogView.spinnerCategory
        etDescription = dialogView.etDescription
        etPrice = dialogView.etPrice

        var categoryAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )
        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerCategory.adapter = categoryAdapter
        //spinnerCategory.setSelection(1)


        dialogBuilder.setView(dialogView)

        val arguments = this.arguments
        // if we are in EDIT mode
         if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
             val todoItem = arguments.getSerializable(ScrollingActivity.KEY_EDIT) as Todo
             etTodoText.setText(todoItem.todoText)
             cbTodoDone.isChecked = todoItem.done
             etDescription.setText(todoItem.Description)
             etPrice.setText(todoItem.price)
             dialogBuilder.setTitle("Edit todo")

             when(todoItem.category){
                 0->spinnerCategory.setSelection(0)
                 1->spinnerCategory.setSelection(1)
                 2->spinnerCategory.setSelection(2)
                 3->spinnerCategory.setSelection(3)
                 4->spinnerCategory.setSelection(4)
                 5->spinnerCategory.setSelection(5)
             }
         }

        dialogBuilder.setPositiveButton("Ok") {
                dialog, which ->
        }
        dialogBuilder.setNegativeButton("Cancel") {
                dialog, which ->
        }


        return dialogBuilder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etTodoText.text.isNotEmpty()) {
                val arguments = this.arguments
                // IF EDIT MODE
                    if (arguments != null && arguments.containsKey(ScrollingActivity.KEY_EDIT)) {
                    handleTodoEdit()
                } else {
                    handleTodoCreate()
                }

                dialog!!.dismiss()
            } else {
                etTodoText.error = "This field can not be empty"
            }
        }
    }




    private fun handleTodoCreate() {
        todoHandler.todoCreated(
            Todo(
                null,
                Date(System.currentTimeMillis()).toString(),
                false,
                etTodoText.text.toString(),
                etDescription.text.toString(),
                etPrice.text.toString(),
                spinnerCategory.selectedItemPosition
            )
        )
    }

    private fun handleTodoEdit() {
        val todoToEdit = arguments?.getSerializable(
            ScrollingActivity.KEY_EDIT
        ) as Todo
        todoToEdit.todoText = etTodoText.text.toString()
        todoToEdit.done = cbTodoDone.isChecked
        todoToEdit.Description = etDescription.text.toString()
        todoToEdit.category = spinnerCategory.selectedItemPosition
        todoToEdit.price = etPrice.text.toString()
        todoHandler.todoUpdated(todoToEdit)
    }


}