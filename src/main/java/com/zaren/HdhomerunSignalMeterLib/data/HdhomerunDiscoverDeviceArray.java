package com.zaren.HdhomerunSignalMeterLib.data;

import java.util.ArrayList;

import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;

public class HdhomerunDiscoverDeviceArray
{
   public static final String NO_ERROR = "none";
   public static final String NETWORK_ERROR = "Network Error";
   
   public ArrayList<HdhomerunDiscoverDevice> mDiscoverDeviceArray;
   private String mError; 
   
   public HdhomerunDiscoverDeviceArray()
   {
      mError = NO_ERROR;
      mDiscoverDeviceArray = new ArrayList<HdhomerunDiscoverDevice>();
   }
   
   public void insert(int ip_addr_val, long type_val, long id_val, int tuner_id_val)
   {      
	  mDiscoverDeviceArray.add(new HdhomerunDiscoverDevice(ip_addr_val,type_val,id_val,tuner_id_val));
	   
      HDHomerunLogger.d(mDiscoverDeviceArray.get(mDiscoverDeviceArray.size()-1).toString() );
   }

   public HdhomerunDiscoverDevice get( int aIndex )
   {
      return mDiscoverDeviceArray.get( aIndex );
   }
   
   /**
    * @return the count
    */
   public int getCount()
   {
      return mDiscoverDeviceArray.size();
   }
   
   public int find( String aDeviceName ) throws DeviceNotFoundException
   {
      for( int i=0; i < mDiscoverDeviceArray.size(); i++ )
      {
         HdhomerunDiscoverDevice theDevice = mDiscoverDeviceArray.get( i );
         if( theDevice.getName().equals( aDeviceName ) )
         {
            return i;
         }
      }
      
      throw new DeviceNotFoundException( "Device " + aDeviceName + " not in discovery list" );
   }

   /**
    * @return the discoverDeviceList
    */
   public ArrayList<HdhomerunDiscoverDevice> getDiscoverDeviceList()
   {
      return mDiscoverDeviceArray;
   }

   public String getError()
   {
      return mError;
   }

   public void setError( )
   {
      this.mError = NETWORK_ERROR;
   }
   
   public void clear()
   {
      mDiscoverDeviceArray.clear();
   }
}
