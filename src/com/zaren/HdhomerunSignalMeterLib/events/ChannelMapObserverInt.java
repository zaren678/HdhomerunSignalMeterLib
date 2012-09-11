package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceResponse;

public interface ChannelMapObserverInt
{
   void channelMapChanged( DeviceResponse aResponse, DeviceController aDeviceController, String aNewChannelMap );   
}
