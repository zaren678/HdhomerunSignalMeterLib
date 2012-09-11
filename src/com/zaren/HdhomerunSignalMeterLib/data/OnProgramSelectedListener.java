package com.zaren.HdhomerunSignalMeterLib.data;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;

public class OnProgramSelectedListener implements OnItemSelectedListener
{
   private DeviceController ctrl;
   public OnProgramSelectedListener(DeviceController c)
   {
      ctrl = c;
   }

   @Override
   public void onItemSelected(AdapterView<?> parent, View view, int pos,
         long id)
   {
      ChannelScanProgram programSelected;
      
      HDHomerunLogger.d("OnProgramSelectedListener: pos " + pos);
      
      programSelected = (ChannelScanProgram)parent.getItemAtPosition(pos);
      ctrl.setProgram( programSelected.programNumber );
   }

   @Override
   public void onNothingSelected(AdapterView<?> parent)
   {
      // Do Nothing
   }

}
