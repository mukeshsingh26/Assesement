package com.java.apppackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.java.custompackage.PackageInfo

class MainAdapter() :
    RecyclerView.Adapter<PackageViewHolder>() {

    private var appsPackageList: List<PackageInfo> = listOf()

    override fun onBindViewHolder(viewHolder: PackageViewHolder, i: Int) {
        viewHolder.bindView(appsPackageList[i])
    }

    override fun getItemCount(): Int {
        return appsPackageList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.launcher_row, parent, false)
        return PackageViewHolder(view)
    }

    fun setItem(list: List<PackageInfo>) {
        appsPackageList = list
        notifyDataSetChanged()
    }

    fun getItem(): List<PackageInfo> {
        return appsPackageList
    }


    var originalData: List<PackageInfo> = ArrayList()

    fun updateList(updatedList: List<PackageInfo>) {
        appsPackageList = updatedList
        notifyDataSetChanged()
    }

}

class PackageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private lateinit var packageInfo: PackageInfo

    private var launchIcon: ImageView = itemView.findViewById(R.id.app_icon) as ImageView
    private var appName: TextView = itemView.findViewById(R.id.app_name)
    private var packageName: TextView = itemView.findViewById(R.id.package_name)
    private var versionName: TextView = itemView.findViewById(R.id.version_name)
    private var versionCode: TextView = itemView.findViewById(R.id.version_code)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val intent =
            v.context.packageManager.getLaunchIntentForPackage(packageInfo.packageName.toString())
        v.context.startActivity(intent)
    }

    fun bindView(info: PackageInfo) {
        packageInfo = info
        appName.text = info.packageLabel
        launchIcon.setImageDrawable(info.packageIcon)
        packageName.text = info.packageName
        versionName.text = info.packageVersion
        versionCode.text = info.packageVersionCode
    }
}
