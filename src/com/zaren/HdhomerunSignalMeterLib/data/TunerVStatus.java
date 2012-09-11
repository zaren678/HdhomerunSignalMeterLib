package com.zaren.HdhomerunSignalMeterLib.data;

public class TunerVStatus
{
   public String vChannel;
   public String name;
   public String auth;
   public String cci;
   public String cgms;
   public boolean notSubscribed;
   public boolean notAvailable;
   public boolean copyProtected;
   public int returnStatus;
   public int vChannelNum;
   
   /**
    * @param channel
    * @param lockStr
    * @param signalPresent
    * @param lockSupported
    * @param lockUnsupported
    * @param signalStrength
    * @param snrQuality
    * @param symbolErrorQuality
    * @param rawBitsPerSecond
    * @param packetsPerSecond
    */
   public TunerVStatus(String channel, String name, String auth, String cci,
         String cgms, boolean notSubscribed, boolean notAvailable, boolean copyProtected, int returnStatus)
   {
      this.vChannel = channel;
      
      setVchannelNum(channel);
      
      this.name = name;
      this.auth = auth;
      this.cci = cci;
      this.cgms = cgms;
      this.notSubscribed = notSubscribed;
      this.notAvailable = notAvailable;
      this.copyProtected = copyProtected;
      this.returnStatus = returnStatus;
   }
   
   private void setVchannelNum(String channel)
   {
      try
      {
         vChannelNum = Integer.parseInt(channel);
      }
      catch (NumberFormatException e)
      {
         vChannelNum = 0;
      }
   }

   public TunerVStatus()
   {
      this.vChannel = "none";
      this.vChannelNum = 0;
      this.name = "none";
      this.notSubscribed = true;
      this.notAvailable = true;
      this.copyProtected = false;
   }

   public void setAllFields(String channel, String name, String auth, String cci,
         String cgms, boolean notSubscribed, boolean notAvailable, boolean copyProtected, int returnStatus)
   {
      this.vChannel = channel;
      
      setVchannelNum(channel);
      
      this.name = name;
      this.auth = auth;
      this.cci = cci;
      this.cgms = cgms;
      this.notSubscribed = notSubscribed;
      this.notAvailable = notAvailable;
      this.copyProtected = copyProtected;
      this.returnStatus = returnStatus;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "TunerVStatus [auth=" + auth + ", cci=" + cci + ", cgms=" + cgms
            + ", copyProtected=" + copyProtected + ", name=" + name
            + ", notAvailable=" + notAvailable + ", notSubscribed="
            + notSubscribed + ", returnStatus=" + returnStatus + ", vChannel="
            + vChannel + "]";
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((auth == null) ? 0 : auth.hashCode());
      result = prime * result + ((cci == null) ? 0 : cci.hashCode());
      result = prime * result + ((cgms == null) ? 0 : cgms.hashCode());
      result = prime * result + (copyProtected ? 1231 : 1237);
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (notAvailable ? 1231 : 1237);
      result = prime * result + (notSubscribed ? 1231 : 1237);
      result = prime * result + returnStatus;
      result = prime * result + ((vChannel == null) ? 0 : vChannel.hashCode());
      result = prime * result + vChannelNum;
      return result;
   }

   @Override
   public boolean equals( Object obj )
   {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      TunerVStatus other = (TunerVStatus) obj;
      if (auth == null)
      {
         if (other.auth != null) return false;
      }
      else if (!auth.equals(other.auth)) return false;
      if (cci == null)
      {
         if (other.cci != null) return false;
      }
      else if (!cci.equals(other.cci)) return false;
      if (cgms == null)
      {
         if (other.cgms != null) return false;
      }
      else if (!cgms.equals(other.cgms)) return false;
      if (copyProtected != other.copyProtected) return false;
      if (name == null)
      {
         if (other.name != null) return false;
      }
      else if (!name.equals(other.name)) return false;
      if (notAvailable != other.notAvailable) return false;
      if (notSubscribed != other.notSubscribed) return false;
      if (returnStatus != other.returnStatus) return false;
      if (vChannel == null)
      {
         if (other.vChannel != null) return false;
      }
      else if (!vChannel.equals(other.vChannel)) return false;
      if (vChannelNum != other.vChannelNum) return false;
      return true;
   }
   
   
   
}
