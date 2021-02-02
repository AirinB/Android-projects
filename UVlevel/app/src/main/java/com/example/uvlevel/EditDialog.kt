package com.example.uvlevel


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.uvlevel.data.AppDatabase
import com.example.uvlevel.data.User
import com.example.uvlevel.data.UserHandler
import com.example.uvlevel.ui.dashboard.DashboardFragment
import kotlinx.android.synthetic.main.edit_dialog.*
import kotlinx.android.synthetic.main.edit_dialog.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.util.*

class EditDialog : DialogFragment() {

    lateinit var cbFreckles: CheckBox
    lateinit var spinnerSkin: Spinner
    lateinit var spinnerEyes: Spinner
    lateinit var spinnerHair: Spinner
    lateinit var spinnerGender: Spinner

    lateinit var userHandler: UserHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is UserHandler){
            userHandler = context
        } else {
            throw RuntimeException(
                    "The Activity is not implementing the ItemHandler interface.")
        }
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        dialogBuilder.setTitle("Edit your Phototype")
        val dialogView = requireActivity().layoutInflater.inflate(
            R.layout.edit_dialog, null
        )

        cbFreckles = dialogView.chFrekles
        spinnerSkin = dialogView.spinnerSkin
        spinnerHair = dialogView.spinnerHair
        spinnerGender = dialogView.spinnerGender
        spinnerEyes = dialogView.spinnerEyes

        var skinAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.skin_color,
            android.R.layout.simple_spinner_item
        )
        skinAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerSkin.adapter = skinAdapter

        var eyeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.eye_color,
            android.R.layout.simple_spinner_item
        )
        skinAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerEyes.adapter = eyeAdapter

        var hairAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.hair_color,
            android.R.layout.simple_spinner_item
        )
        hairAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerHair.adapter = hairAdapter

        var genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerGender.adapter = genderAdapter

        dialogBuilder.setView(dialogView)



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

//            val arguments = this.arguments
//            if (arguments != null){
//                var user = handleTodoCreate()
//                Thread {
//                    val context: Context = this.context ?: MainActivity() as Context
//                    val database = AppDatabase.getInstance(context = context).userDAO()
//                    database.updateUser(user)
//                }
//
//                val dashFragment = arguments?.getSerializable(
//                        "DASH_FRAGMENT"
//                ) as DashboardFragment
//                dashFragment.updateData()
//            }
            val user: User  = handleTodoCreate()
            userHandler.userUpdated(user)
            dialog!!.dismiss()
        }
    }

    private fun handleTodoCreate(): User {
        print("Saving the state")
        val userType = arguments?.getSerializable(
                "USER_TYPE"
        ) as User

        userType.eyesColor = spinnerEyes.selectedItem.toString()
        userType.hairColor = spinnerHair.selectedItem.toString()
        userType.skinColor = spinnerSkin.selectedItem.toString()
        userType.gender = spinnerGender.selectedItem.toString()
        userType.freckles = cbFreckles.isChecked

        return userType

    }
}

