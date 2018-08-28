package com.zaren.HdhomerunSignalMeterLib.v2.discover

import com.zaren.HdhomerunSignalMeterLib.data.HdhomerunDiscoverDeviceArray

class DiscoverDevices {
    companion object {
        init {
            System.loadLibrary("hdhomerun")
        }
    }

    external fun discoverDevices(): HdhomerunDiscoverDeviceArray
}