package com.paola.example.custompicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import java.util.*

class MainActivity : AppCompatActivity(), CustomPicker.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customPicker = findViewById<View>(R.id.main_custom) as CustomPicker
        customPicker.setOnItemSelectedListener(this)

        val list: MutableList<Any> = ArrayList()
        list.add("ciao!")
        list.add("pippo!")
        list.add("luca!")

        customPicker.setData(list)


    }

    override fun onItemSelected(picker: CustomPicker?, data: Any?, position: Int) {
       val textSelected = data as String
       Log.d(MainActivity::class.java.simpleName, "$textSelected")
    }
}