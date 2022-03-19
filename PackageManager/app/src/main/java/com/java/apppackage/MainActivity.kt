package com.java.apppackage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.java.custompackage.CustomPackageManager
import com.java.custompackage.PackageInfo
import com.java.custompackage.PackageUpdateProvider


class MainActivity : AppCompatActivity(), PackageUpdateProvider {

    private lateinit var mRecyclerView : RecyclerView
    private var mPackageManager: CustomPackageManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPackageManager = CustomPackageManager(this)
        mPackageManager!!.registerPackageUpdateProvider(this)

        mRecyclerView = findViewById(R.id.rvMain)
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        val adapter = MainAdapter(mPackageManager!!.getAllInstalledAppsPackage())
        mRecyclerView.adapter = adapter
    }

    override fun packageUpdated(packageList: ArrayList<PackageInfo>) {
        mRecyclerView.adapter = MainAdapter(packageList)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPackageManager!!.unRegisterPackageUpdateProvider()
    }
}