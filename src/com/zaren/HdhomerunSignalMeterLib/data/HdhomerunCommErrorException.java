package com.zaren.HdhomerunSignalMeterLib.data;

public class HdhomerunCommErrorException extends Exception
{

   private static final long serialVersionUID = 1L;
   private String error;
   
   public HdhomerunCommErrorException(String error)
   {
      super();
      this.error = error;
   }

   public String getError()
   {
      return error;
   }

}
