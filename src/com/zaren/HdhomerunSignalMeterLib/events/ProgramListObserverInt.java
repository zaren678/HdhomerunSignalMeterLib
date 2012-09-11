package com.zaren.HdhomerunSignalMeterLib.events;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.ProgramsList;

public interface ProgramListObserverInt
{
   void programListChanged( DeviceController aDeviceController, ProgramsList aPrograms, int aChannel );
}
