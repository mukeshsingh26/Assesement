// IOrientationInterface.aidl
package com.java.orientation;

// Declare any non-default types here with import statements

interface IOrientationInterface {
    /**
    Get sensor orientation information
    */
        int[] getOrientationData();
}