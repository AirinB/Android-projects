package com.example.uvlevel.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.uvlevel.EditDialog
import com.example.uvlevel.R
import com.example.uvlevel.data.AppDatabase
import com.example.uvlevel.MainActivity
import com.example.uvlevel.data.User
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.io.Serializable

class DashboardFragment : Fragment(), Serializable {

    private lateinit var dashboardViewModel: DashboardViewModel

    private  var skinColor : String = "Fair"
    private  var eyesColor: String = "Green"
    private  var hairColor: String = "Blonde"
    private  var gender: String = "Female"
    private var freckles: Boolean = false

    private lateinit var user: User


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val editButton: Button = root.findViewById(R.id.btnEdit)
        val tvSkinRisk: TextView = root.findViewById(R.id.tvInformation)
        val riskButton: Button = root.findViewById(R.id.btnRisk)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {

        })


        val context: Context = this.context ?: MainActivity() as Context

        Thread {
            val database = AppDatabase.getInstance(context = context).userDAO()
            val users = database.getUsers()
            if (users.size == 1){
                user = users[0]
                skinColor = user.skinColor
                eyesColor = user.eyesColor
                hairColor = user.hairColor
                gender    = user.gender
                freckles  = user.freckles
            }else{
                user = User(1, skinColor, eyesColor, hairColor, freckles, gender)

                database.insertUser(user)
            }
            activity?.runOnUiThread {
                tvSkin.text = skinColor
                tvEyes.text = eyesColor
                tvHair.text = hairColor
//                val type = getskinType()
//                if (type  == 1 ||type == 2){
//                    tvInformation.text = getString(R.string.risk_type12)
//
//                }else if(type == 3 || type == 4){
//
//                    tvInformation.text = getString(R.string.risk_type34)
//
//                }else {
//                    tvInformation.text = getString(R.string.risk_type56)
//                }
            }
        }.start()

        riskButton.setOnClickListener {
            Thread{
                val database = AppDatabase.getInstance(context = context).userDAO()
                val users = database.getUsers()
                if (users.size == 1) {
                    user = users[0]

                    activity?.runOnUiThread {
                        val type = getskinType()
                        if (type  == 1 ||type == 2){
                            tvInformation.text = getString(R.string.risk_type12)

                        }else if(type == 3 || type == 4){

                            tvInformation.text = getString(R.string.risk_type34)

                        }else {
                            tvInformation.text = getString(R.string.risk_type56)
                        }
                    }

                }
            }.start()
        }


        editButton.setOnClickListener {
            Thread{
                val editDialog = EditDialog()

                val bundle = Bundle()
                bundle.putSerializable("USER_TYPE", user)
                bundle.putSerializable("DASH_FRAGMENT", this)
                editDialog.arguments = bundle

                getFragmentManager()?.let { it1 -> editDialog.show(it1, "Phototype dialg") }
            }.start()
        }
        return root
    }


    fun getskinType(): Int {
        var type = 1
        if (user.freckles && (user.skinColor == "Fair" ||user.skinColor == "White")
                && (user.hairColor == "Blond" || user.hairColor == "Red")){
            type = 1
        } else if ((user.skinColor == "Fair" ||user.skinColor == "White")
                && (user.eyesColor == "Blue" || user.eyesColor == "Green")){
            type = 2
        }else if ((user.skinColor == "Tanned" ||user.skinColor == "White")){
            type = 3
        }else if(user.skinColor == "Brown"){
            type = 4
        }else if(user.skinColor == "Dark brown"){
            type = 5
        }else if(user.skinColor == "Black"){
            type = 6
        }
        return type
    }



}