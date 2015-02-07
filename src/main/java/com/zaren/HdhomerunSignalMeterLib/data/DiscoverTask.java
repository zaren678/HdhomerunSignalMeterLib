package com.zaren.HdhomerunSignalMeterLib.data;

import com.zaren.HdhomerunSignalMeterLib.data.HdhomerunDiscoverDeviceArray;
import com.zaren.HdhomerunSignalMeterLib.ui.IndeterminateProgressBarInt;
import com.zaren.HdhomerunSignalMeterLib.util.ErrorHandler;

import android.os.AsyncTask;

public class DiscoverTask extends AsyncTask<Void, Void, HdhomerunDiscoverDeviceArray>
{

   private IndeterminateProgressBarInt mProgressBar;
   private DeviceListInt mDeviceList;

   static
   {
      System.loadLibrary("hdhomerun");
   }
   
   /**
    * @param mProgressBar
    */
   public DiscoverTask(IndeterminateProgressBarInt aProgressBar, DeviceListInt aDeviceList)
   {
      super();
      mProgressBar = aProgressBar;
      mDeviceList = aDeviceList;
   }

   private native HdhomerunDiscoverDeviceArray discover();
   
   @Override
   protected HdhomerunDiscoverDeviceArray doInBackground(Void... params)
   {
      HdhomerunDiscoverDeviceArray discoverDeviceArray = discover();
      
      return discoverDeviceArray;
   }

   /* (non-Javadoc)
    * @see android.os.AsyncTask#onCancelled()
    */
   @Override
   protected void onCancelled()
   {
      mProgressBar.setProgressBarBusy( false );
      super.onCancelled();
   }

   /* (non-Javadoc)
    * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
    */
   @Override
   protected void onPostExecute(HdhomerunDiscoverDeviceArray result)
   {
      String theError = result.getError();
      int theNumDevices = result.getCount();
      
      if(theError != HdhomerunDiscoverDeviceArray.NO_ERROR)
      {
         //network Error
         ErrorHandler.HandleError("Discover Error: " + theError);
         //return;
      }
      else if(theNumDevices == 0)
      {
         //no Devices
         ErrorHandler.HandleError("No HdHomeRun Devices Found on Network");
         //return;
      }
      mDeviceList.setDeviceList(result);
      mProgressBar.setProgressBarBusy( false );
      super.onPostExecute(result);
   }

   /* (non-Javadoc)
    * @see android.os.AsyncTask#onPreExecute()
    */
   @Override
   protected void onPreExecute()
   {
      mProgressBar.setProgressBarBusy( true );
      super.onPreExecute();
   }


}
