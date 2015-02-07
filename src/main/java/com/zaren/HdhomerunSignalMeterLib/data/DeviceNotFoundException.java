package com.zaren.HdhomerunSignalMeterLib.data;

public class DeviceNotFoundException extends Exception
{
   /**
    * 
    */
   private static final long serialVersionUID = 2455043232492729098L;   
   private String error;
   
   public DeviceNotFoundException(String error)
   {
      super();
      this.error = error;
   }

   public String getError()
   {
      return error;
   }

}
