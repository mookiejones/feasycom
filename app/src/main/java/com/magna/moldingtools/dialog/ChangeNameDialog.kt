package com.magna.moldingtools.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.magna.moldingtools.R
import kotlinx.android.synthetic.main.change_name_layout.*
import kotlinx.android.synthetic.main.change_name_layout.view.*
import java.lang.IllegalStateException

class ChangeNameDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val view = LayoutInflater.from(it).inflate(R.layout.change_name_layout,null)
            val name = requireArguments().getString("name")!!.replace("Molding_Tools","",true)
            view.txtNameChange.setText(name)
            var newName=name
            view.txtNameChange.doAfterTextChanged {value->
                newName=value.toString()
            }
            builder
                    .setTitle("Change Name")
                    .setView(view)
                    .setPositiveButton(android.R.string.ok) { dialog, id ->
                        Log.e("ChangeName", "ChangeName")

                        Log.e("ChangeName", "ChangeName $newName")
                    }
                    .setNegativeButton(android.R.string.cancel,{dialog,id ->
                       dialog.dismiss()
                    })

             .create()

        }?: throw IllegalStateException("Activity must not be null")
    }
}