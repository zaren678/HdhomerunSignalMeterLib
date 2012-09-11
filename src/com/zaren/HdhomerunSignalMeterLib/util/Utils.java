package com.zaren.HdhomerunSignalMeterLib.util;

import com.zaren.HdhomerunSignalMeterLib.data.HdhomerunDevice;

public class Utils
{
   public static byte[] ipAddressToByteArray( int ipAddress )
   {
      byte[] theByteArray = new byte[4];
      
      theByteArray[0] = (byte) (ipAddress & 0xFF);
      theByteArray[1] = (byte) (ipAddress >> 8 & 0xFF);
      theByteArray[2] = (byte) (ipAddress >> 16 & 0xFF);
      theByteArray[3] = (byte) (ipAddress >> 24 & 0xFF);
      
      return theByteArray;
   }
   
   
   /**
    * The hdhr reports its IP address as an integer but the bytes and swapped compared to
    * How Android reports its integer IP addresses from the WIFI manager... thats annoying
    * @param ipAddress
    * @return
    */
   public static byte[] HdHrIpAddressToByteArray( int ipAddress )
   {
      byte[] theByteArray = new byte[4];
      
      theByteArray[3] = (byte) (ipAddress & 0xFF);
      theByteArray[2] = (byte) (ipAddress >> 8 & 0xFF);
      theByteArray[1] = (byte) (ipAddress >> 16 & 0xFF);
      theByteArray[0] = (byte) (ipAddress >> 24 & 0xFF);
      
      return theByteArray;
   }
   
   public static String getChannelStringFromTunerStatusChannel(HdhomerunDevice aDevice, String aChannel, String aLockStr)
   {
      HDHomerunLogger.v("getChannelNumberFromTunerStatusChannel: "+aChannel + " " + aLockStr);
      if(aChannel.equals("none"))
      {
         return aChannel;
      }
      else
      {
         String[] splitString = aChannel.split(":");
         if(splitString.length > 1)
         {
            int channel_int = Integer.parseInt(splitString[1]);
            if(channel_int > 1000)
            {
               //this must be a frequency value
               channel_int = aDevice.frequencyToChannelNumber(channel_int);
               
               if(channel_int == 0)
               {
                  //for some reason we couldn't match up the frequency to channel number, just return the frequency
                  return aLockStr + ":" + splitString[1];
               }
               
               return aLockStr + ":" + channel_int;
            }
            else
            {
               return aLockStr + ":" + splitString[1];
            }
         }
         else
         {
            //something was wrong with the channel from the device
            return "none";
         }
      }
   }
   
   public static int getChannelNumberFromTunerStatusChannel( HdhomerunDevice aDevice, String aChannel, String aLockStr )
   {
      HDHomerunLogger.v( "getChannelNumberFromTunerStatusChannel: "+aChannel + " " + aLockStr );
      if( aChannel.equals( "none" ) )
      {
         return -1;
      }
      else
      {
         String[] splitString = aChannel.split( ":" );
         if( splitString.length > 1 )
         {
            int theChannelInt = Integer.parseInt( splitString[1] );
            if( theChannelInt > 1000 )
            {
               //this must be a frequency value
               theChannelInt = aDevice.frequencyToChannelNumber( theChannelInt );
               
               if( theChannelInt == 0 )
               {
                  //for some reason we couldn't match up the frequency to channel number, just return the frequency
                  return -1;
               }
               
               return theChannelInt;
            }
            else
            {
               return theChannelInt;
            }
         }
         else
         {
            //something was wrong with the channel from the device
            return -1;
         }
      }
   }
   
   
}
