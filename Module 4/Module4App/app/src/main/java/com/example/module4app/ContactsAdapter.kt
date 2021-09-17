package com.example.module4app

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes


open class ContactsAdapter(private val mContext: Context, @SuppressLint("SupportAnnotationUsage") @LayoutRes list: ArrayList<MyContacts>) :
    ArrayAdapter<MyContacts>(mContext, 0, list) {
    private var mList: List<MyContacts> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView
        if (listItem == null) listItem =
            LayoutInflater.from(mContext).inflate(R.layout.rowcontactsload, parent, false)
        val currentMovie = mList[position]

        val name = listItem!!.findViewById(R.id.name_tv) as TextView
        name.setText(currentMovie.contactName)

        val release = listItem.findViewById(R.id.number_tv) as TextView
        release.text = currentMovie.contactNumber.toString()

        return listItem

    }

    init {
        mList = list
    }


}