package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.TunerStatus;

public interface ChannelLockedObserverInt
{
   void channelLocked( DeviceController aDeviceController, TunerStatus aTunerStatus );
}
