package com.zaren.HdhomerunSignalMeterLib.events;

import java.io.Serializable;

import android.database.Observable;

public class ObservableWithCheck< T > extends Observable< T > implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = 510868949221761073L;

   @Override
   public void unregisterObserver( T aObserver )
   {
      if( mObservers.contains( aObserver ) )
      {
         super.unregisterObserver( aObserver );
      }
   }
   
}
