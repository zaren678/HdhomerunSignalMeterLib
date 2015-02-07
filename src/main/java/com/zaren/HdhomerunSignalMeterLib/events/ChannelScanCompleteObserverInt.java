package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceResponse;

public interface ChannelScanCompleteObserverInt
{   
   void channelScanComplete( DeviceResponse aResponse, DeviceController aDeviceController );
}
