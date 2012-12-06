package com.zaren.HdhomerunSignalMeterLib.data;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.zaren.HdhomerunSignalMeterLib.ui.HdhomerunSignalMeterUiInt;
import com.zaren.HdhomerunSignalMeterLib.ui.IndeterminateProgressBarInt;
import com.zaren.HdhomerunSignalMeterLib.util.ErrorHandler;

public class OnDeviceSelectedListener implements OnItemSelectedListener
{

   private DeviceController mCtrl;
   private HdhomerunSignalMeterUiInt mUi;
   private IndeterminateProgressBarInt mProgressBar;

   public OnDeviceSelectedListener( HdhomerunSignalMeterUiInt aUi, IndeterminateProgressBarInt aProgressBar )
   {
      super();
      mUi = aUi;
      mProgressBar = aProgressBar;
   }

   public void onItemSelected( AdapterView< ? > aParent, View aView, int aPosition, long aId )
   {
      HdhomerunDiscoverDevice discoverDeviceSelected = (HdhomerunDiscoverDevice) aParent.getItemAtPosition( aPosition );

      // stop the old control thread
      mCtrl = mUi.getCntrl();
      if( mCtrl != null )
      {
         mCtrl.requestStop();
         mCtrl.destroyDevice();
      }

      try
      {
         mCtrl = new DeviceController( discoverDeviceSelected, mProgressBar, mUi.getContext() );

         mCtrl.waitForDeviceReady();
         mCtrl.initialize( true );
         mCtrl.startTunerStatusUpdates();

         mUi.setCntrl( mCtrl );
      }
      catch( HdhomerunCommErrorException e )
      {
         ErrorHandler.HandleError( "Failed to set device" );
      }

   } // end onItemSelected

   public void onNothingSelected( AdapterView< ? > parent )
   {
      // do nothing

   }

} // end class OnDeviceSElectedListener
