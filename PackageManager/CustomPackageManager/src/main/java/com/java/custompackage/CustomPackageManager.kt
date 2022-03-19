package com.java.custompackage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import java.lang.ref.WeakReference
import java.util.Collections.sort
import android.content.pm.PackageManager
import android.widget.Toast


class CustomPackageManager(pContext: Context) : BroadcastReceiver() {

    companion object {
        private const val TAG = "CustomPackageManager"
        private var packageListener: PackageUpdateProvider? = null
    }
    private val mContext: Context = pContext

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.data?.encodedSchemeSpecificPart
        packageListener?.packageUpdated(getAllInstalledAppsPackage())
        if (isPackageExisted(packageName)) {
            if (BuildConfig.DEBUG) Log.d(TAG, "$packageName is installed !!!")
            Toast.makeText(context, "$packageName is installed !!!", Toast.LENGTH_LONG)
                .show()
        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "$packageName is removed !!!")
            Toast.makeText(context, "$packageName is removed !!!", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun getAllInstalledAppsPackage(): ArrayList<PackageInfo> {

        val appsPackageList = arrayListOf<PackageInfo>()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        mContext.packageManager?.let { pManager ->

            val allPackages = pManager.queryIntentActivities(intent, 0)
            allPackages.map { info ->
                val packageInfo = PackageInfo()
                packageInfo.packageIcon = info.activityInfo.loadIcon(pManager)
                packageInfo.packageLabel = info.loadLabel(pManager).toString()
                packageInfo.packageName = info.activityInfo.packageName
                val mPackageInfo = pManager.getPackageInfo(info.activityInfo.packageName, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.packageVersionCode = mPackageInfo.longVersionCode.toString()
                } else {
                    packageInfo.packageVersionCode = mPackageInfo.versionCode.toString()
                }
                packageInfo.packageVersion = mPackageInfo.versionName
                appsPackageList.add(packageInfo)
            }
        }
        return sort(appsPackageList)
    }

    private fun sort(packageList: ArrayList<PackageInfo>): ArrayList<PackageInfo> {
        packageList.sortWith(Comparator { o1, o2 ->
            return@Comparator o1.packageLabel?.compareTo(o2.packageLabel ?: "") ?: 0
        })
        return packageList
    }

    fun registerPackageUpdateProvider(pPackageListener: PackageUpdateProvider) {
        packageListener = pPackageListener
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        mContext.registerReceiver(this, intentFilter)
    }

    fun unRegisterPackageUpdateProvider() {
        mContext.unregisterReceiver(this)
        packageListener = null
    }

    private fun isPackageExisted(targetPackage: String?): Boolean {
        val pm = mContext.packageManager
        try {
            val info: android.content.pm.PackageInfo? =
                pm?.getPackageInfo(targetPackage!!, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }
}

class PackageInfo {

    var packageIcon: Drawable? = null
    var packageLabel: String? = null
    var packageName: String? = null
    var packageVersionCode: String? = null
    var packageVersion: String? = null

    override fun equals(other: Any?): Boolean {
        if (other is PackageInfo) {
            return packageLabel?.equals(other.packageLabel) ?: false
        }
        return false
    }

    override fun hashCode(): Int {
        var result = packageIcon?.hashCode() ?: 0
        result = 31 * result + (packageLabel?.hashCode() ?: 0)
        result = 31 * result + (packageName?.hashCode() ?: 0)
        result = 31 * result + (packageVersionCode?.hashCode() ?: 0)
        result = 31 * result + (packageVersion?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "PackageInfo(packageIcon=$packageIcon, packageLabel=$packageLabel, packageName=$packageName, packageVersionCode=$packageVersionCode, packageversion=$packageVersion)"
    }


}
