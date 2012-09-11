package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceResponse;
import com.zaren.HdhomerunSignalMeterLib.data.TunerStatus;

public interface TunerStatusObserverInt
{
   void tunerStatusChanged( DeviceResponse aResponse, DeviceController aDeviceController, TunerStatus aTunerStatus );
}
