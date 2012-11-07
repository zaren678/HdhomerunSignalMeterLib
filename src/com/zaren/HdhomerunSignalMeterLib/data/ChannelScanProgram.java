package com.zaren.HdhomerunSignalMeterLib.data;

import java.io.Serializable;

public class ChannelScanProgram implements Serializable
{
   private static final long serialVersionUID = 3131125398380181596L;
   public String programString = "";
   public int programNumber = 0;
   public int virtualMajor = 0;
   public int virtualMinor = 0;
   public int type = 0;
   public String name = "";
   boolean mIsSubscribed = true;
   
   public ChannelScanProgram()
   {
      
   }
   
   public ChannelScanProgram(String aProgramString, 
         int aProgramNumber,
         int aVirtualMajor, 
         int aVirtualMinor,
         int aType,
         String aName)
   {      
      programString = aProgramString;
      programNumber = aProgramNumber;
      virtualMajor = aVirtualMajor;
      virtualMinor = aVirtualMinor;
      type = aType;
      name = aName;
   }
   
   public ChannelScanProgram( String aProgramString, 
         int aProgramNumber,
         int aVirtualMajor, 
         int aVirtualMinor,
         int aType,
         String aName,
         boolean aIsSubscribed )
   {      
      programString = aProgramString;
      programNumber = aProgramNumber;
      virtualMajor = aVirtualMajor;
      virtualMinor = aVirtualMinor;
      type = aType;
      name = aName;
      mIsSubscribed = aIsSubscribed;
   }

   public void setAllFields(String programString,
                            int programNumber,
                            int virtualMajor,
                            int virtualMinor,
                            int type,
                            String name)
   {
      this.programString = programString;
      this.programNumber = programNumber;
      this.virtualMajor = virtualMajor;
      this.virtualMinor = virtualMinor;
      this.type = type;
      this.name = name;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + programNumber;
      result = prime * result + type;
      result = prime * result + virtualMajor;
      result = prime * result + virtualMinor;
      return result;
   }

   @Override
   public boolean equals( Object obj )
   {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ChannelScanProgram other = (ChannelScanProgram) obj;
      if (name == null)
      {
         if (other.name != null) return false;
      }
      else if (!name.equals(other.name)) return false;
      if (programNumber != other.programNumber) return false;
      if (type != other.type) return false;
      if (virtualMajor != other.virtualMajor) return false;
      if (virtualMinor != other.virtualMinor) return false;
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      if(virtualMinor == 0)
      {
         return programNumber + ": " + virtualMajor + " " +name;
      }
      
      return programNumber + ": " + virtualMajor + "." + virtualMinor+ " " +name;
   }

   public void setVirtualChannelStatus( TunerVStatus aVirtualChannelStatus )
   {
      if( aVirtualChannelStatus != null &&
          aVirtualChannelStatus.returnStatus > 0 )
      {
         mIsSubscribed = !aVirtualChannelStatus.notSubscribed;
         name = aVirtualChannelStatus.name;
      }
   }

   public boolean isSubscribed()
   {
      return mIsSubscribed;
   }
   
}
