package com.zaren.HdhomerunSignalMeterLib.data;

import java.util.HashMap;

public class DeviceResponse
{
   public static final int COMMUNICATION_ERROR = -1;
   public static final int FAILURE = 0;
   public static final int SUCCESS = 1;
   
   public static final String KEY_LOCKED = "Locked";
   public static final String KEY_ERROR = "Error";
   public static final String KEY_ACTION = "Action";
   
   private HashMap< String, Object > mInfo = new HashMap< String, Object >();
   private int mStatus;
   
   public DeviceResponse( int aStatus )
   {
      mStatus = aStatus;
   }
   
   public int getStatus()
   {
      return mStatus;
   }

   public void setStatus( int aStatus )
   {
      mStatus = aStatus;
   }

   public void putString( String aKey, String aString )
   {
      mInfo.put( aKey, aString );
   }
   
   public String getString( String aKey )
   {
      if( mInfo.containsKey( aKey ) )
      {
         Object theObject = mInfo.get( aKey );
         
         if( theObject instanceof String )
         {
            return (String)theObject;
         }         
      }
      
      return null;      
   }
   
   public void putBoolean( String aKey, Boolean aBoolean )
   {
      mInfo.put( aKey, aBoolean );
   }
   
   public boolean getBoolean( String aKey, boolean aDefault )
   {
      if( mInfo.containsKey( aKey ) )
      {
         Object theObject = mInfo.get( aKey );
         
         if( theObject instanceof Boolean )
         {
            return (Boolean)theObject;
         }         
      }
      
      return aDefault;      
   }

}
