package com.zaren.HdhomerunSignalMeterLib.data;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Looper;

import com.zaren.HdhomerunSignalMeterLib.events.DeviceControllerEvents;
import com.zaren.HdhomerunSignalMeterLib.ui.IndeterminateProgressBarInt;
import com.zaren.HdhomerunSignalMeterLib.util.ErrorHandler;
import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;
import com.zaren.HdhomerunSignalMeterLib.util.Utils;

/**
 * this class will monitor signal strength and perform higher level functions
 * 
 * @author john
 * 
 */
public class DeviceController implements Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = 1858437034303215606L;
   public static final long SLEEP_TIME_BETWEEN_TASKS = 50;
   private HdhomerunDevice mDevice;
   private boolean mDeviceReady = false;
   private boolean mDeviceThreadReady = false;
   
   private DeviceControllerEvents mEvents;

   private Thread mDeviceThread;
   private long mStatusUpdateTime = 500;
   private volatile Handler mDeviceHandler;
   private volatile Handler mUiHandler;
   private IndeterminateProgressBarInt mProgressBar;
   private volatile ChannelScanRunnable mChannelScanTask;
   private volatile TunerStatus mTunerStatus;
   private TunerStatus mPreviousTunerStatus;
   private volatile boolean mTunerStatusTaskRunning;
   private volatile boolean mStopNow = false;
   private transient ChannelList mChannelList = new ChannelList(); //this is transient because it is not serializable
   private volatile String mCurrentChannelMap;

   public DeviceController(HdhomerunDiscoverDevice discoverDevice, IndeterminateProgressBarInt aProgressBar)
   {
      mCurrentChannelMap = "none";
      mEvents = new DeviceControllerEvents();
      mProgressBar = aProgressBar;
      mTunerStatus = new TunerStatus();
      mPreviousTunerStatus = new TunerStatus();
      mUiHandler = new Handler();
      
      setDevice(discoverDevice);
      
      //Lets kick off the main thread for the hdhomerunDevice
      mDeviceThread = new Thread(new Runnable()
         {
            public void run() 
            {
               
               try 
               {
                  Looper.prepare();
                  mDeviceHandler = new Handler();
                  mDeviceThreadReady = true;
                                    
                  HDHomerunLogger.i("Device Thread entering the loop");
                  
                  Looper.loop();
                  
                  HDHomerunLogger.i("Device Thread exiting gracefully");
               }
               catch (Throwable t) 
               {
                  HDHomerunLogger.e("Device Thread halted due to an error " + t);
               } 
            }
         }, "Device: " + mDevice.getDeviceName() );
      mDeviceThread.start(); 
   }

   public void initialize( final boolean aReportInitialStatus )
   {
      mDeviceHandler.post(new Runnable()
      {
         @Override
         public void run()
         {
            HDHomerunLogger.d("Initializing the channelmap");
            //initialize the channelmap
            final String theCurrentChannelMap = mDevice.getCurrentChannelMap();
            mCurrentChannelMap = theCurrentChannelMap;
            final String[] theChannelMaps = mDevice.getChannelMaps();
            
            int theSetChannelMapIdx = -1;
            for(int i=0; i< theChannelMaps.length; i++)
            {
               if(theChannelMaps[i].equals(theCurrentChannelMap))
               {
                  theSetChannelMapIdx = i;
               }
            }
            
            //had to make this variable because setChannelMapIdx had to be final to be used
            //in the Runnable below, but if it was final I couldn't set it in the loop above
            int theChannelMapIdx = theSetChannelMapIdx;
                        
            notifyChannelMapListChanged( theChannelMaps );            
            
            DeviceResponse theResponse = new DeviceResponse( DeviceResponse.SUCCESS );
            if( theChannelMapIdx > -1 )
            {
               HDHomerunLogger.d("Setting initial channelmap spinner to " + theChannelMapIdx + " " + theCurrentChannelMap);
               mCurrentChannelMap = theCurrentChannelMap;
               
               if( aReportInitialStatus )
               {
                  notifyChannelMapChanged( theResponse, theCurrentChannelMap );
               }
            }
            else
            {
               HDHomerunLogger.d("No initial channelmap");
               mCurrentChannelMap = theChannelMaps[ 0 ];
               if( aReportInitialStatus )
               {
                  notifyChannelMapChanged( theResponse, theChannelMaps[ 0 ] );
               }
            }
            
            final ProgramsList thePrograms = new ProgramsList();
            mDevice.getTunerStreamInfo( thePrograms );
            
            //get the program number
            final int theInitialChannel = mDevice.getCurrentChannel();
            
            JniString theProgram = new JniString();
            mDevice.getTunerProgram( theProgram );
            final int theIntialProgram = Integer.parseInt( theProgram.getString() );          

            notifyObserversProgramListChanged( thePrograms, theInitialChannel );
            
            if( theIntialProgram > 0 && aReportInitialStatus )
            {               
               notifyObserversProgramChanged( theResponse, thePrograms.get( theIntialProgram ) );
            }
            
            setProgressBarBusy( false );

            
             //Seems like the device needs a little time before it does something else here
             try
             {
                Thread.sleep(SLEEP_TIME_BETWEEN_TASKS);
             }
             catch (InterruptedException e)
             {
                e.printStackTrace();
             }
         }
      });
   }
   public void startTunerStatusUpdates()
   {
      HDHomerunLogger.d("startTunerStatusUpdates: deviceHandler " + mDeviceHandler);
      
      mTunerStatusTaskRunning = true;
      mDeviceHandler.post( new TunerStatusRunnable() );
   }   

   public synchronized void requestStop() 
   {
      // using the handler, post a Runnable that will quit()
      // the Looper attached to our DownloadThread
      // obviously, all previously queued tasks will be executed
      // before the loop gets the quit Runnable
            
      mDeviceHandler.removeCallbacksAndMessages( null );
      
      if(mDeviceThread.isAlive() == true)
      {
         mDeviceHandler.post(new Runnable() 
         {
            @Override
            public void run() 
            {
               HDHomerunLogger.i("Device Thread loop quitting by request");
               
               Looper.myLooper().quit();
            }
         });
         
         //now join the thread to make sure we're stopped
         try
         {
            mDeviceThread.join();
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }
   }
   
   synchronized public void setChannelMap(String newMap)
   {
      if(mDeviceReady == false)
      {
         return;
      }
      
      HDHomerunLogger.d("setChannelMap: newMap " + newMap);
      
      setProgressBarBusy( true );
      
      mDeviceHandler.post(new SetChannelMapRunnable(newMap));
   }
   
   private class SetChannelMapRunnable implements Runnable
   {
      private String mNewMap;      

      public SetChannelMapRunnable(String aNewMap)
      {
         mNewMap = aNewMap;
      }

      @Override
      public void run()
      {       
         String thePrevChannelMap;
         boolean tunerLocked = false;
         JniString theError = new JniString();
         
         DeviceResponse theResponse = new DeviceResponse( DeviceResponse.SUCCESS );
         theResponse.putString(DeviceResponse.KEY_ACTION, "setting channel map");

         if(mStopNow == true)
         {
            return;
         }
         
         try
         {
            theResponse.setStatus( mDevice.tunerLockeyRequest(theError) );
            
            if(theResponse.getStatus() > 0)
            {
               tunerLocked = true;
               
               // set the new map
               thePrevChannelMap = mDevice.getCurrentChannelMap();
               
               HDHomerunLogger.d("SetChannelMapRunnable: new map: "+mNewMap+" old map: "+thePrevChannelMap);
               
               if(!mNewMap.equals(thePrevChannelMap))
               {
                  theResponse.setStatus( mDevice.setChannelMap(mNewMap) );
                              
                  if (theResponse.getStatus() > 0)
                  {
                     mDevice.createChannelList( mNewMap, DeviceController.this.getChannelList() );
                     
                     HDHomerunLogger.d( DeviceController.this.getChannelList().toString() );
                     
                     mCurrentChannelMap = mNewMap;
                     
                  }
               }
            }
            else
            {
               //Tuner was locked
               HDHomerunLogger.d("SetChannelMapRunnable: " + theError);
               
               fillOutLockedResponse( theResponse );
            }
         
         }
         finally
         {
            if( tunerLocked )
            {
               mDevice.tunerLockeyRelease();
            }
            
            notifyChannelMapChanged( theResponse, mCurrentChannelMap );
            setProgressBarBusy( false );
            
            //Seems like the device needs a little time before it does something else here
            try
            {
               Thread.sleep(SLEEP_TIME_BETWEEN_TASKS);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
         }
      }
   }
   
   private class TunerStatusRunnable implements Runnable
   {
      @Override
      public void run()
      {
         if(mStopNow == true)
         {
            return;
         }
         
         HDHomerunLogger.v("Update Tuner Status: device id: "
               + mDevice.getDeviceName());

         mDevice.updateTunerStatus(mTunerStatus);

         HDHomerunLogger.v("Update Tuner Status: return status "
               + mTunerStatus.returnStatus);
         
         final DeviceResponse theResponse = new DeviceResponse( mTunerStatus.returnStatus );
         theResponse.putString(DeviceResponse.KEY_ACTION, "Getting Tuner Status");

         JniString theChannel = new JniString();
         theResponse.setStatus( mDevice.getTunerChannel( theChannel ) );
         
         final CurrentChannelAndProgram theCurrentChannel = new CurrentChannelAndProgram();
         if( theResponse.getStatus() == DeviceResponse.SUCCESS )
         {
            String theRetChannel = theChannel.getString();
            theCurrentChannel.setChannel( theRetChannel );
            
            JniString theProgram = new JniString();
            theResponse.setStatus( mDevice.getTunerProgram( theProgram ) );
            
            if( theResponse.getStatus() == DeviceResponse.SUCCESS )
            {
               int theRetProgram = 0;
               
               try
               {
                  theRetProgram = Integer.parseInt( theProgram.getString() );
               }
               catch( NumberFormatException e )
               {
                  HDHomerunLogger.d( "Failed to parse program num from string " + theProgram );
               }
               
               theCurrentChannel.setProgramNum( theRetProgram );
               
               ProgramsList thePrograms = new ProgramsList();
               mDevice.getTunerStreamInfo( thePrograms );
               
               theCurrentChannel.setPrograms( thePrograms );
            }                        
         }

         
         mUiHandler.post(new Runnable()
         {
            @Override
            public void run()
            {               
               notifyObserversTunerStatus( theResponse, mTunerStatus, theCurrentChannel );
            }
         });
         

         //schedule the next run
         if(mTunerStatusTaskRunning == true)
         {
            mDeviceHandler.postDelayed(new TunerStatusRunnable(),mStatusUpdateTime);
         }
      }
   }

   public void setDevice(HdhomerunDiscoverDevice discoverDevice)
   {
      // Clean up the old HDHR Device
      if (mDevice != null)
      {
         mDevice.destroy();
         mDevice = null;
      }

      // create new device
      try
      {
         mDevice = new HdhomerunDevice(discoverDevice.id, discoverDevice.ip_addr,
               discoverDevice.tuner_id);
         
         mDeviceReady = true;
         
         //get the channelMap to build the channelList
         String currentChannelMap = mDevice.getCurrentChannelMap();
         mDevice.createChannelList(currentChannelMap, mChannelList);
         
         HDHomerunLogger.d( getChannelList().toString() );
         
         //get the tuner status to set the channelEditText and channelMap
         mDevice.updateTunerStatus(mTunerStatus);
         
         
         int channel = Utils.getChannelNumberFromTunerStatusChannel(mDevice, mTunerStatus.channel);
         
         DeviceResponse theResponse = new DeviceResponse( DeviceResponse.SUCCESS );
         notifyObserversChannelChanged( theResponse, channel );

      }
      catch (HdhomerunCommErrorException e)
      {
         if( mDevice != null )
         {
            mDevice.destroy();
            mDevice = null;
         }
         ErrorHandler.HandleError("Failed to set device");
      }
   }

   public void setTunerChannel(int channel, boolean virtualTune)
   {  
      if(mDevice == null)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDevice.getcPointer() == -1)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDeviceReady == false)
      {
         return;
      }
      
      if(mDevice.getDeviceType().equals(HdhomerunDevice.DEVICE_ATSC))
      {
         if((channel > mChannelList.getMaxNumber()) ||
            (channel < mChannelList.getMinNumber()) )
         {
            ErrorHandler.HandleError("Channel Out of Range: Valid range for this channelmap is " + mChannelList.getMinNumber() + " to " + mChannelList.getMaxNumber());
            return;
         }
         else
         {
            HDHomerunLogger.d("setTunerChannel: channel " + channel);
            
            setProgressBarBusy( true );
            mDeviceHandler.post( new SetChannelRunnable( this, channel ) );
         }
      }
      
      else if( (mDevice.getDeviceType().equals(HdhomerunDevice.DEVICE_CABLECARD)))
      {                 
         if(virtualTune == true)
         {
            //this means its a virtual channel number
            setProgressBarBusy( true );
            mDeviceHandler.post( new SetVChannelRunnable( this, channel ) );
         }
         else
         {
            if((channel > mChannelList.getMaxNumber()) ||
               (channel < mChannelList.getMinNumber()) )
            {
               ErrorHandler.HandleError("Channel Out of Range: Valid range for this channelmap is " + mChannelList.getMinNumber() + " to " + mChannelList.getMaxNumber());
               return;
            }
            else
            {
               HDHomerunLogger.d("setTunerChannel: channel " + channel);
               
               mProgressBar.setProgressBarBusy( true );
               mDeviceHandler.post(new SetChannelRunnable( this, channel ) );
            }
         }
      }
   }            

   public void cancelChannelScan( )
   {
      if( mChannelScanTask != null )
      {
         mChannelScanTask.stop();
      }
   }   

   public void channelScanForward()
   {
      channelScanForward( -1 );
   }
   
   public void channelScanForward( int aStartingChannel )
   {
      if(mDevice == null)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDevice.getcPointer() == -1)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDeviceReady == false)
      {
         return;
      }
      
      //if the channelscan is already running forward then don't do anything
      if( (mChannelScanTask != null) &&
          (mChannelScanTask.isRunning() == true) &&
          (mChannelScanTask.isForward() == true))
      {
         return;
      }
      
      //if the channelscan is already running but running backward then stop the channel scan
      if( (mChannelScanTask != null) &&
          (mChannelScanTask.isRunning() == true) &&
          (mChannelScanTask.isForward() == false))
      {
         mChannelScanTask.stop();
         return;
      }
      
      setProgressBarBusy( true );
      mChannelScanTask = new ChannelScanRunnable(true, this, mChannelList, aStartingChannel);
      mDeviceHandler.post(mChannelScanTask);
   }
   
   public void fullChannelScan()
   {
      if( mChannelScanTask != null &&
          mChannelScanTask.isRunning() )
      {
         return;
      }
      
      setProgressBarBusy( true );
      mChannelScanTask = new ChannelScanRunnable( this, mChannelList );
      mDeviceHandler.post(mChannelScanTask);
   }
   
   public void channelScanBackward()
   {
      channelScanBackward( -1 );
   }
   
   public void channelScanBackward( int aStartingChannel )
   {
      if(mDevice == null)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDevice.getcPointer() == -1)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDeviceReady == false)
      {
         return;
      }
      
      //if the channelscan is already running backward then don't do anything
      if( (mChannelScanTask != null) &&
          (mChannelScanTask.isRunning() == true) &&
          (mChannelScanTask.isForward() == false))
      {
         return;
      }
      
      //if the channelscan is already running but running forward then stop it
      if( (mChannelScanTask != null) &&
          (mChannelScanTask.isRunning() == true) &&
          (mChannelScanTask.isForward() == true))
      {
         mChannelScanTask.stop();
         return;
      }
      
      setProgressBarBusy( true );
      mChannelScanTask = new ChannelScanRunnable(false, this, mChannelList, aStartingChannel);
      mDeviceHandler.post(mChannelScanTask);
   }

   public void pause()
   {
      HDHomerunLogger.d("Control: pause");
      mTunerStatusTaskRunning = false;
   }
   
   public void resume()
   {
      HDHomerunLogger.d("Control: resume");
      mTunerStatusTaskRunning = true;
      mDeviceHandler.post( new TunerStatusRunnable() );
   }

   public void destroyDevice()
   {
      if(mDevice != null)
      {
         mDevice.tunerLockeyRelease();
         mDevice.destroy();
      }
      mDeviceReady = false;
      
      mEvents.unregisterAll();
   }

   /**
    * @param channelList the channelList to set
    */
   public void setChannelList(ChannelList channelList)
   {
      this.mChannelList = channelList;
   }

   /**
    * @return the channelList
    */
   public ChannelList getChannelList()
   {
      return mChannelList;
   }
   
   /**
    * @return the device
    */
   public HdhomerunDevice getDevice()
   {
      return mDevice;
   }

   public void setProgram(int aProgramNumber)
   {
      if(mDevice == null)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDevice.getcPointer() == -1)
      {
         ErrorHandler.HandleError("No Device Set");
         return;
      }
      
      if(mDeviceReady == false)
      {
         return;
      }
      
      setProgressBarBusy( true );
      mDeviceHandler.post( new SetProgramRunnable( aProgramNumber ) );
   }
   
   /**
    * @author john
    *
    */
   public class SetProgramRunnable implements Runnable
   {
      private int mProgram;      
      
      public SetProgramRunnable( int aProgramNumber )
      {
         mProgram = aProgramNumber;         
      }

      /* (non-Javadoc)
       * @see java.lang.Runnable#run()
       */
      @Override
      public void run()
      {
         int theStatus;
         JniString theError = new JniString();

         if(mStopNow == true)
         {
            return;
         }
         
         theStatus = mDevice.tunerLockeyRequest(theError);
         
         DeviceResponse theResponse = new DeviceResponse( theStatus );
         theResponse.putString(DeviceResponse.KEY_ACTION, "setting program" );
         
         final ProgramsList thePrograms = new ProgramsList();
         mDevice.getTunerStreamInfo( thePrograms );         
         JniString thePrevProgramStr = new JniString();
         mDevice.getTunerProgram( thePrevProgramStr  );
         
         int thePrevProgramNum = Integer.parseInt( thePrevProgramStr.getString() );
         
         ChannelScanProgram thePrevProgram = null;
         
         ChannelScanProgram theNewProgram = null;
         for( ChannelScanProgram theProg : thePrograms )
         {
            if( theProg.programNumber == thePrevProgramNum )
            {
               thePrevProgram = theProg;
            }
            
            if( theProg.programNumber == mProgram )
            {
               theNewProgram = theProg;
            }
         }
                                    
         if(theStatus > 0)
         {
            mDevice.setTunerProgram(mProgram + "");
            mDevice.tunerLockeyRelease();
            
            notifyObserversProgramChanged(theResponse, theNewProgram);
         }
         else
         {
            //Tuner was locked
            HDHomerunLogger.d("SetProgramRunnable: " + theError);
            
            fillOutLockedResponse( theResponse );
            
            notifyObserversProgramChanged(theResponse, thePrevProgram);            
         }
         
         setProgressBarBusy( false );
         
         //Seems like the device needs a little time before it does something else here
         try
         {
            Thread.sleep(SLEEP_TIME_BETWEEN_TASKS);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         
      }      

   }

   public int setTargetIP(String protocol, String serverIpAddress, int serverVideoPort) throws UnknownHostException
   {

      InetAddress address = InetAddress.getByName(serverIpAddress);
      
      return mDevice.setTargetIP(protocol + "://"+address.getHostAddress()+":"+serverVideoPort);
   }
   
   public String getCurrentChannelMap()
   {
      return mCurrentChannelMap;
   }
   
   public void fillOutLockedResponse( DeviceResponse aResponse )
   {
      String theOwner = mDevice.getLockkeyOwner();
      aResponse.putString( DeviceResponse.KEY_ERROR, "tuner Locked by " + theOwner );
      aResponse.putBoolean( DeviceResponse.KEY_LOCKED, true );      
   }
   
   public void notifyObserversTunerStatus( final DeviceResponse aResponse, final TunerStatus aTunerStatus, final CurrentChannelAndProgram aCurrentChannel )
   {
      if( !aTunerStatus.equals( mPreviousTunerStatus ) )
      {
         mUiHandler.post( new Runnable()
         {         
            @Override
            public void run()
            {
               mEvents.notifyTunerStatusChanged(aResponse, DeviceController.this, aTunerStatus, aCurrentChannel);       
            }
         });               
      }
      mPreviousTunerStatus.clone( mTunerStatus );      
   }

   public void notifyObserversProgramListChanged( final ProgramsList thePrograms, final int mChannel )
   {
      mUiHandler.post( new Runnable()
      {         
         @Override
         public void run()
         {
            mEvents.notifyProgramListChanged( DeviceController.this, thePrograms, mChannel );
         }
      });  
   }

   public void notifyChannelLocked(final TunerStatus aTunerStatus)
   {
      mUiHandler.post( new Runnable()
      {         
         @Override
         public void run()
         {
            mEvents.notifyChannelLocked( DeviceController.this, aTunerStatus );
         }
      });  
   }

   public void notifyChannelScanComplete(final DeviceResponse aResponse)
   {
      mUiHandler.post( new Runnable()
      {         
         @Override
         public void run()
         {
            mEvents.notifyChannelScanComplete( aResponse, DeviceController.this );
         }
      }); 
   }
   
   private void notifyChannelMapChanged( final DeviceResponse theResponse, final String aNewChannelMap )
   {
      mUiHandler.post( new Runnable()
      {         
         @Override
         public void run()
         {
            mEvents.notifyChannelMapChanged( theResponse, DeviceController.this, aNewChannelMap );
         }
      }); 
   }
   
   private void notifyChannelMapListChanged( final String[] aChannelMapArray )
   {
      mUiHandler.post( new Runnable()
      {         
         @Override
         public void run()
         {
            mEvents.notifyChannelMapListChanged( DeviceController.this, aChannelMapArray );
         }
      });       
   }
   
   public void notifyObserversProgramChanged( final DeviceResponse aResponse, final ChannelScanProgram aChannelScanProgram )
   {
      mUiHandler.post( new Runnable()
      {         
         @Override
         public void run()
         {
            mEvents.notifyProgramChanged( aResponse, DeviceController.this, aChannelScanProgram );
         }
      });       
   }
   
   public void notifyObserversChannelChanged( final DeviceResponse aResponse, final int aChannel )
   {
      mUiHandler.post(new Runnable()
      {
         @Override
         public void run()
         {
            mEvents.notifyChannelChanged( aResponse, DeviceController.this, aChannel );
         }
      });      
   }
   
   public void setProgressBarBusy( final boolean aIsBusy )
   {
      mUiHandler.post(new Runnable()
      {
         @Override
         public void run()
         {
            mProgressBar.setProgressBarBusy( aIsBusy );
         }
      });
   }
   
   public DeviceControllerEvents events()
   {
      return mEvents;
   }

   public void waitForDeviceReady()
   {
      while( mDeviceReady != true && mDeviceThreadReady != true && mDeviceHandler == null );
      
      try
      {
         Thread.sleep(SLEEP_TIME_BETWEEN_TASKS);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }
   
} // end class HDHRCtrl
