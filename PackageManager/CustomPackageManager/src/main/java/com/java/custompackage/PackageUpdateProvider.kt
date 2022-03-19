package com.java.custompackage

interface PackageUpdateProvider {

    fun packageUpdated(packageList: ArrayList<PackageInfo>)
}