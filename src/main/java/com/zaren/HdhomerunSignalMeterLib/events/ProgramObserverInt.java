package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.ChannelScanProgram;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceResponse;

public interface ProgramObserverInt
{     
   void programChanged( DeviceResponse aResponse, DeviceController aDeviceController, ChannelScanProgram aChannelScanProgram );
}
