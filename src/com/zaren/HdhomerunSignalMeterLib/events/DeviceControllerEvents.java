package com.zaren.HdhomerunSignalMeterLib.events;

import java.util.ArrayList;

import android.database.Observable;

import com.zaren.HdhomerunSignalMeterLib.data.ChannelScanProgram;
import com.zaren.HdhomerunSignalMeterLib.data.CurrentChannelAndProgram;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;
import com.zaren.HdhomerunSignalMeterLib.data.DeviceResponse;
import com.zaren.HdhomerunSignalMeterLib.data.ProgramsList;
import com.zaren.HdhomerunSignalMeterLib.data.TunerStatus;

public class DeviceControllerEvents
{
   private ChannelMapObservable mChannelMapChanged;
   private ChannelMapListObservable mChannelMapListChanged;
   private ChannelChangedObserver mChannelChanged;
   private ChannelLockedObserver mChannelLocked;
   private ChannelScanCompleteObserver mChannelScanComplete;
   private ProgramListObserver mProgramListChanged;
   private ProgramObserver mProgramChanged;
   private TunerStatusObserver mTunerStatusChanged;

   public DeviceControllerEvents()
   {
      mChannelMapChanged = new ChannelMapObservable();
      mChannelMapListChanged = new ChannelMapListObservable();
      mChannelChanged = new ChannelChangedObserver();
      mChannelLocked = new ChannelLockedObserver();
      mChannelScanComplete = new ChannelScanCompleteObserver();
      mProgramListChanged = new ProgramListObserver();
      mProgramChanged = new ProgramObserver();
      mTunerStatusChanged = new TunerStatusObserver();
   }

   public void unregisterAll()
   {
      mChannelMapChanged.unregisterAll();
      mChannelMapListChanged.unregisterAll();
      mChannelChanged.unregisterAll();
      mChannelLocked.unregisterAll();
      mChannelScanComplete.unregisterAll();
      mProgramListChanged.unregisterAll();
      mProgramChanged.unregisterAll();
      mTunerStatusChanged.unregisterAll();      
   }
   
   public Observable< ChannelMapObserverInt > channelMapChanged()
   {
      return mChannelMapChanged;
   }
   
   public void notifyChannelMapChanged( DeviceResponse aResponse, DeviceController aDeviceController, String aNewChannelMap )
   {
      mChannelMapChanged.raiseEvent( aResponse, aDeviceController, aNewChannelMap );
   }
   
   public Observable< ChannelMapListChangedObserverInt > channelMapListChanged()
   {
      return mChannelMapListChanged;
   }
   
   public void notifyChannelMapListChanged( DeviceController aDeviceController, String[] aChannelMapList )
   {
      mChannelMapListChanged.raiseEvent( aDeviceController, aChannelMapList );
   }
   
   public Observable< ChannelChangedObserverInt > channelChanged()
   {
      return mChannelChanged;
   }
   
   public void notifyChannelChanged( DeviceResponse aResponse, DeviceController aDeviceController, int aNewChannel )
   {
      mChannelChanged.raiseEvent( aResponse, aDeviceController, aNewChannel );
   }
   
   public Observable< ChannelLockedObserverInt > channelLocked()
   {
      return mChannelLocked;
   }
   
   public void notifyChannelLocked( DeviceController aDeviceController, TunerStatus aTunerStatus )
   {
      mChannelLocked.raiseEvent( aDeviceController, aTunerStatus );
   }
   
   public Observable< ChannelScanCompleteObserverInt > channelScanComplete()
   {
      return mChannelScanComplete;
   }
   
   public void notifyChannelScanComplete( DeviceResponse aResponse, DeviceController aDeviceController )
   {
      mChannelScanComplete.raiseEvent( aResponse, aDeviceController );
   }
   
   public Observable< ProgramListObserverInt > programListChanged()
   {
      return mProgramListChanged;
   }
   
   public void notifyProgramListChanged( DeviceController aDeviceController, ProgramsList thePrograms, int aChannel )
   {
      mProgramListChanged.raiseEvent( aDeviceController, thePrograms, aChannel );
   }
   
   public Observable< ProgramObserverInt > programChanged()
   {
      return mProgramChanged;
   }
   
   public void notifyProgramChanged( DeviceResponse aResponse, DeviceController aDeviceController, ChannelScanProgram aProgram )
   {
      mProgramChanged.raiseEvent( aResponse, aDeviceController, aProgram );
   }
   
   public Observable< TunerStatusObserverInt > tunerStatusChanged()
   {
      return mTunerStatusChanged;
   }
   
   public void notifyTunerStatusChanged( DeviceResponse aResponse, DeviceController aDeviceController, TunerStatus aTunerStatus, CurrentChannelAndProgram aCurrentChannel )
   {
      mTunerStatusChanged.raiseEvent( aResponse, aDeviceController, aTunerStatus, aCurrentChannel );
   }
   
   private class ChannelMapObservable extends ObservableWithCheck< ChannelMapObserverInt >
   {
      public void raiseEvent( DeviceResponse aResponse, DeviceController aDeviceController, String aNewChannelMap )
      {
         for( ChannelMapObserverInt theObserver : mObservers )
         {
            theObserver.channelMapChanged(aResponse, aDeviceController, aNewChannelMap );
         }
      }
   }
   
   private class ChannelMapListObservable extends ObservableWithCheck< ChannelMapListChangedObserverInt >
   {
      public void raiseEvent( DeviceController aDeviceController, String[] aChannelMapList )
      {
         for( ChannelMapListChangedObserverInt theObserver : mObservers )
         {
            theObserver.channelMapListChanged( aDeviceController, aChannelMapList );
         }
      }
   }
   
   private class ChannelChangedObserver extends ObservableWithCheck< ChannelChangedObserverInt >
   {
      public void raiseEvent( DeviceResponse aResponse, DeviceController aDeviceController, int aNewChannel )
      {
         for( ChannelChangedObserverInt theObserver : mObservers )
         {
            theObserver.channelChanged( aResponse, aDeviceController, aNewChannel );
         }
      }
   }
   
   private class TunerStatusObserver extends ObservableWithCheck< TunerStatusObserverInt >
   {
      public void raiseEvent( DeviceResponse aResponse, DeviceController aDeviceController, TunerStatus aTunerStatus, CurrentChannelAndProgram aCurrentChannel )
      {
         for( TunerStatusObserverInt theObserver : mObservers )
         {
            theObserver.tunerStatusChanged(aResponse, aDeviceController, aTunerStatus, aCurrentChannel);
         }
      }
   }
   
   private class ProgramObserver extends ObservableWithCheck< ProgramObserverInt >
   {
      public void raiseEvent( DeviceResponse aResponse, DeviceController aDeviceController, ChannelScanProgram aProgram )
      {
         //workaround for concurrent exception while hitting play, the handler of this observer unregisters itself...
         ArrayList< ProgramObserverInt > theObservers = new ArrayList< ProgramObserverInt >( mObservers );     
         
         for( ProgramObserverInt theObserver : theObservers )
         {
            theObserver.programChanged( aResponse, aDeviceController, aProgram );
         }
      }
   }
   
   private class ProgramListObserver extends ObservableWithCheck< ProgramListObserverInt >
   {
      public void raiseEvent( DeviceController aDeviceController, ProgramsList thePrograms, int aChannel )
      {
         for( ProgramListObserverInt theObserver : mObservers )
         {
            theObserver.programListChanged( aDeviceController, thePrograms, aChannel );
         }
      }
   }
   
   private class ChannelScanCompleteObserver extends ObservableWithCheck< ChannelScanCompleteObserverInt >
   {             
      public void raiseEvent( DeviceResponse aResponse, DeviceController aDeviceController )
      {
         //workaround for concurrent exception while hitting play, the handler of this observer unregisters itself...
         ArrayList< ChannelScanCompleteObserverInt > theObservers = new ArrayList< ChannelScanCompleteObserverInt >( mObservers );
         
         for( ChannelScanCompleteObserverInt theObserver : theObservers )
         {
            theObserver.channelScanComplete( aResponse, aDeviceController );
         }
      }
   }
   
   private class ChannelLockedObserver extends ObservableWithCheck< ChannelLockedObserverInt >
   {
      public void raiseEvent( DeviceController aDeviceController, TunerStatus aTunerStatus )
      {
         for( ChannelLockedObserverInt theObserver : mObservers )
         {
            theObserver.channelLocked( aDeviceController, aTunerStatus );
         }
      }
   }   
}
