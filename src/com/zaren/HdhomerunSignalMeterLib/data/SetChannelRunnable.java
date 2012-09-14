package com.zaren.HdhomerunSignalMeterLib.data;

import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;

public class SetChannelRunnable implements Runnable
{

   int mChannel;
   DeviceController mDeviceController;
   HdhomerunDevice mDevice;
   
   public SetChannelRunnable( DeviceController aController, int aChannel)
   {
      mChannel = aChannel;
      mDeviceController = aController;
      mDevice = aController.getDevice();
   }
   
   @Override
   public void run()
   {
      int theStatus;
      JniString theError = new JniString();
      ProgramsList thePrograms = new ProgramsList();
      TunerStatus theTunerStatus = new TunerStatus();
      

      /*if(mStopNow == true)
      {
         return;
      }*/
      
      theStatus = mDevice.tunerLockeyRequest(theError);
      
      final DeviceResponse theResponse = new DeviceResponse( theStatus );
      theResponse.putString(DeviceResponse.KEY_ACTION, "setting Channel");
      
      boolean theGotLock = false;
      try
      {
         if( theResponse.getStatus() != DeviceResponse.SUCCESS )
         {
            //Tuner was locked
            mDeviceController.fillOutLockedResponse( theResponse );
            return;
         }
         
         theGotLock = true;
                  
         theResponse.setStatus( mDevice.setTunerChannel( "auto:" + mChannel ) );
         HDHomerunLogger.d("Tune channel Status " + theStatus);
         
         if( theResponse.getStatus() != DeviceResponse.SUCCESS )
         {            
            theResponse.putString( DeviceResponse.KEY_ERROR, "unable to tune to channel " + mChannel );
            return;
         }
         
         theResponse.setStatus( mDevice.waitForLock(theTunerStatus) );
         HDHomerunLogger.d("Wait for lock status  " + theStatus);               

         if( theResponse.getStatus() != DeviceResponse.SUCCESS )
         {
            // error waiting for lock
            theResponse.putString( DeviceResponse.KEY_ERROR, "unable to lock onto channel " + mChannel );
            return;
         }
                  
         mDeviceController.notifyChannelLocked( theTunerStatus );
         mDeviceController.notifyObserversTunerStatus( theResponse, theTunerStatus, null );
         
         mDevice.getTunerStreamInfo(thePrograms);            
         mDeviceController.notifyObserversProgramListChanged( thePrograms, mChannel );                                                                                      
      }
      finally
      {
         if( theGotLock )
         {
            mDevice.tunerLockeyRelease();
         }
         
         mDeviceController.notifyObserversChannelChanged( theResponse, mChannel );
         mDeviceController.setProgressBarBusy( false );
         
         //Seems like the device needs a little time before it does something else here
         try
         {
            Thread.sleep( DeviceController.SLEEP_TIME_BETWEEN_TASKS );
         }
         catch (InterruptedException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } 
      }

      return;
   }

}
