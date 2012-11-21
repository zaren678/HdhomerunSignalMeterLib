package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceResponse;

public interface VChannelChangedObserverInt
{
   void vChannelChanged( DeviceResponse aResponse, DeviceController aDeviceController, String aChannel );
}

