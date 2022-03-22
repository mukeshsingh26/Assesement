package com.java.apppackage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.java.custompackage.CustomPackageManager
import com.java.custompackage.PackageInfo
import com.java.custompackage.PackageUpdateProvider


class MainActivity : AppCompatActivity(), PackageUpdateProvider {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var appName: EditText
    private lateinit var adapter: MainAdapter
    private var mPackageManager: CustomPackageManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPackageManager = CustomPackageManager(this)
        mPackageManager!!.registerPackageUpdateProvider(this)

        appName = findViewById(R.id.app_title_search)
        mRecyclerView = findViewById(R.id.rvMain)
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = MainAdapter()
        adapter.setItem(mPackageManager!!.getAllInstalledAppsPackage())
        mRecyclerView.adapter = adapter
        appName.addTextChangedListener(textWatcher)

        adapter.originalData = adapter.getItem()
    }

    override fun packageUpdated(packageList: ArrayList<PackageInfo>) {
       adapter.setItem(packageList.toList())
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if ((s.toString().length > 1) && (s.toString().isNotEmpty())
            ) {
                filter(s.toString())
            } else if(s.toString().isEmpty()) {
                adapter.updateList(adapter.originalData)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    fun filter(text: String?) {
        var temp: List<PackageInfo> = ArrayList()
        adapter.setItem(adapter.originalData)
        for (d in adapter.getItem()) {
            if (d.packageLabel.toString().lowercase().contains(text.toString().lowercase())) {
                temp = temp.plusElement(d)
            }
        }
        adapter.updateList(temp)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPackageManager!!.unRegisterPackageUpdateProvider()
    }
}