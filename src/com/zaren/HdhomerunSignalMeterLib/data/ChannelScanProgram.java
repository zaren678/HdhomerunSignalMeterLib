package com.zaren.HdhomerunSignalMeterLib.data;

import java.io.Serializable;

public class ChannelScanProgram implements Serializable
{
   private static final long serialVersionUID = 3131125398380181596L;

   public static final String PROGRAM_ENCRYPTED = "encrypted";
   public static final String PROGRAM_CONTROL = "control";
   public static final String PROGRAM_NODATA = "no data";
   public static final String PROGRAM_NORMAL = "normal";
   public static final String PROGRAM_VCHANNEL = "vchannel";  
   
   public String programString = "";
   public int programNumber = 0;
   public int virtualMajor = 0;
   public int virtualMinor = 0;
   public String type = PROGRAM_NORMAL;
   public String name = "";
   boolean mIsSubscribed = true;
   
   public ChannelScanProgram()
   {
      
   }
   
   public ChannelScanProgram(String aProgramString, 
         int aProgramNumber,
         int aVirtualMajor, 
         int aVirtualMinor,
         String aType,
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
         String aType,
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
                            String type,
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
      result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
      result = prime * result + programNumber;
      result = prime * result + ( ( type == null ) ? 0 : type.hashCode() );
      result = prime * result + virtualMajor;
      result = prime * result + virtualMinor;
      return result;
   }

   @Override
   public boolean equals( Object obj )
   {
      if( this == obj ) return true;
      if( obj == null ) return false;
      if( getClass() != obj.getClass() ) return false;
      ChannelScanProgram other = (ChannelScanProgram) obj;
      if( name == null )
      {
         if( other.name != null ) return false;
      }
      else if( !name.equals( other.name ) ) return false;
      if( programNumber != other.programNumber ) return false;
      if( type == null )
      {
         if( other.type != null ) return false;
      }
      else if( !type.equals( other.type ) ) return false;
      if( virtualMajor != other.virtualMajor ) return false;
      if( virtualMinor != other.virtualMinor ) return false;
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder theBuilder = new StringBuilder();
      
      theBuilder.append( programNumber + ": " + virtualMajor );
      
      if( virtualMinor != 0 )
      {
         theBuilder.append( "." + virtualMinor );
      }
      
      theBuilder.append( " " + name );
      
      if( type.equals( PROGRAM_ENCRYPTED ) )
      {
         theBuilder.append( " (" + PROGRAM_ENCRYPTED + ")" );
      }
      else if( type.equals( PROGRAM_NODATA ) )
      {
         theBuilder.append( " (" + PROGRAM_NODATA + ")" );
      }
      else if( type.equals( PROGRAM_CONTROL ) )
      {
         theBuilder.append( " (" + PROGRAM_CONTROL + ")" );
      }
      
      return theBuilder.toString();
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
