package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceResponse;

public interface ChannelChangedObserverInt
{
   void channelChanged( DeviceResponse aResponse, DeviceController aDeviceController, int aCurrentChannel );
}
