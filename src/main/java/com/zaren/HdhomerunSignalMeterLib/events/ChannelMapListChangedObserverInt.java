package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;

public interface ChannelMapListChangedObserverInt
{
   void channelMapListChanged( DeviceController aDeviceController, String[] aChannelMapList );
}
