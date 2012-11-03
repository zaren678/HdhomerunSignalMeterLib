package com.zaren.HdhomerunSignalMeterLib.data;

import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;

public class SetVChannelRunnable implements Runnable
{

   private int mChannel;
   private DeviceController mDeviceController;
   private HdhomerunDevice mDevice;
   
   public SetVChannelRunnable( DeviceController aController, int aChannel )
   {
      mChannel = aChannel;
      mDeviceController = aController;
      mDevice = aController.getDevice();
   }
   @Override
   public void run()
   {
      int theStatus;

      /*if(mStopNow == true)
      {
         return;
      }*/
      
      ProgramsList thePrograms = new ProgramsList();
      TunerStatus theTunerStatus = new TunerStatus();
      int theProgramPosition = -1;
      
      JniString theError = new JniString();
      theStatus = mDevice.tunerLockeyRequest( theError );
      
      final DeviceResponse theResponse = new DeviceResponse( theStatus );
      theResponse.putString(DeviceResponse.KEY_ACTION, "Setting Virtual Channel");
      
      if(theStatus > 0)
      {            
         theStatus = mDevice.setTunerVChannel( "" + mChannel );            
         HDHomerunLogger.d( "Tune Virtual channel Status " + theStatus );
         
         if (theStatus > 0)
         {
            theStatus = mDevice.waitForLock( theTunerStatus );
            HDHomerunLogger.d( "Wait for lock status  " + theStatus );

            if (theStatus > 0)
            {                  
               mDevice.getTunerStreamInfo( thePrograms );
               
               TunerVStatus theTunerVStatus = new TunerVStatus();
               mDevice.updateTunerVStatus( theTunerVStatus );
               HDHomerunLogger.d( theTunerVStatus.toString() );
               
               JniString theProgram = new JniString();
               mDevice.getTunerProgram( theProgram );
               theProgramPosition = Integer.parseInt( theProgram.getString() );                
            }
            else
            {
               theResponse.putString(DeviceResponse.KEY_ERROR, "Unable to lock onto Virtual Channel " + mChannel);
            }
         }
         else
         {
            // error tunning channel
            theResponse.putString(DeviceResponse.KEY_ERROR, "Unable to tune to Virtual Channel " + mChannel);
         }
         
         mDevice.tunerLockeyRelease();
      }
      else
      {
         //Tuner was locked
         mDeviceController.fillOutLockedResponse( theResponse );
      }
      
      theResponse.setStatus( theStatus );
      
      int theFinalProgramPosition = theProgramPosition;

      mDeviceController.notifyObserversTunerStatus( theResponse, theTunerStatus, null );
          
      final int theChannel = mDevice.getCurrentChannel();
      mDeviceController.notifyObserversProgramListChanged( thePrograms, theChannel );
      
      
      if( theFinalProgramPosition > -1 )
      {
         mDeviceController.notifyObserversProgramChanged( theResponse, thePrograms.get( theFinalProgramPosition ) );
      }
      
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

      return;
   }

}
