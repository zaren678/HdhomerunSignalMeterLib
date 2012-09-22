package com.zaren.HdhomerunSignalMeterLib.events;

import android.database.Observable;

public class ObservableWithCheck< T > extends Observable< T >
{

   @Override
   public void unregisterObserver( T aObserver )
   {
      if( mObservers.contains( aObserver ) )
      {
         super.unregisterObserver( aObserver );
      }
   }
   
}
