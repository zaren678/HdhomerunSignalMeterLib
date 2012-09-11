package com.zaren.HdhomerunSignalMeterLib.data;

public class TunerStatus implements Cloneable
{   
   public String channel;
   public String lockStr;
   public boolean signalPresent;
   public boolean lockSupported;
   public boolean lockUnsupported;
   public long signalStrength;
   public long snrQuality;
   public long symbolErrorQuality;
   public long rawBitsPerSecond;
   public long packetsPerSecond;
   public int  returnStatus;
   
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
   public TunerStatus(String channel, String lockStr, boolean signalPresent,
         boolean lockSupported, boolean lockUnsupported, long signalStrength,
         long snrQuality, long symbolErrorQuality, long rawBitsPerSecond,
         long packetsPerSecond, int returnStatus)
   {
      this.channel = channel;
      this.lockStr = lockStr;
      this.signalPresent = signalPresent;
      this.lockSupported = lockSupported;
      this.lockUnsupported = lockUnsupported;
      this.signalStrength = signalStrength;
      this.snrQuality = snrQuality;
      this.symbolErrorQuality = symbolErrorQuality;
      this.rawBitsPerSecond = rawBitsPerSecond;
      this.packetsPerSecond = packetsPerSecond;
      this.returnStatus = returnStatus;
   }
   
   public TunerStatus()
   {
	  this.channel = "none";
      this.lockStr = "none";
      this.signalPresent = false;
      this.lockSupported = false;
      this.lockUnsupported = true;
      this.signalStrength = 0;
      this.snrQuality = 0;
      this.symbolErrorQuality = 0;
      this.rawBitsPerSecond = 0;
      this.packetsPerSecond = 0;
      this.returnStatus = 0;
   }

   public void setAllFields(String channel, String lockStr, boolean signalPresent,
         boolean lockSupported, boolean lockUnsupported, long signalStrength,
         long snrQuality, long symbolErrorQuality, long rawBitsPerSecond,
         long packetsPerSecond, int returnStatus)
   {
	  this.channel = channel;
      this.lockStr = lockStr;
      this.signalPresent = signalPresent;
      this.lockSupported = lockSupported;
      this.lockUnsupported = lockUnsupported;
      this.signalStrength = signalStrength;
      this.snrQuality = snrQuality;
      this.symbolErrorQuality = symbolErrorQuality;
      this.rawBitsPerSecond = rawBitsPerSecond;
      this.packetsPerSecond = packetsPerSecond;
      this.returnStatus = returnStatus;   
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "TunerStatus [channel=" + channel + ", lockStr=" + lockStr
            + ", lockSupported=" + lockSupported + ", lockUnsupported="
            + lockUnsupported + ", packetsPerSecond=" + packetsPerSecond
            + ", rawBitsPerSecond=" + rawBitsPerSecond + ", signalPresent="
            + signalPresent + ", signalStrength=" + signalStrength
            + ", snrQuality=" + snrQuality + ", symbolErrorQuality="
            + symbolErrorQuality + "]";
   }   
   
   public String networkDataRateString()
   {
      return String.format("%.3f Mbps", (packetsPerSecond * 1316 * 8)/1000000.0);
   }
   
   public double networkDataRateVal()
   {
      return (packetsPerSecond * 1316 * 8)/1000000.0;
   }
   
   public String dataRateString()
   {
      return String.format("%.3f Mbps", (rawBitsPerSecond/1000000.0));
   }
   
   public double dataRateVal()
   {
      return rawBitsPerSecond/1000000.0;
   }

   public void clone( TunerStatus aOtherTunerStatus )
   {
      this.channel = aOtherTunerStatus.channel;
      this.lockStr = aOtherTunerStatus.lockStr;
      this.signalPresent = aOtherTunerStatus.signalPresent;
      this.lockSupported = aOtherTunerStatus.lockSupported;
      this.lockUnsupported = aOtherTunerStatus.lockUnsupported;
      this.signalStrength = aOtherTunerStatus.signalStrength;
      this.snrQuality = aOtherTunerStatus.snrQuality;
      this.symbolErrorQuality = aOtherTunerStatus.symbolErrorQuality;
      this.rawBitsPerSecond = aOtherTunerStatus.rawBitsPerSecond;
      this.packetsPerSecond = aOtherTunerStatus.packetsPerSecond;
      this.returnStatus = aOtherTunerStatus.returnStatus;      
   }
   
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((channel == null) ? 0 : channel.hashCode());
      result = prime * result + ((lockStr == null) ? 0 : lockStr.hashCode());
      result = prime * result + (lockSupported ? 1231 : 1237);
      result = prime * result + (lockUnsupported ? 1231 : 1237);
      result = prime * result
            + (int) (packetsPerSecond ^ (packetsPerSecond >>> 32));
      result = prime * result
            + (int) (rawBitsPerSecond ^ (rawBitsPerSecond >>> 32));
      result = prime * result + returnStatus;
      result = prime * result + (signalPresent ? 1231 : 1237);
      result = prime * result
            + (int) (signalStrength ^ (signalStrength >>> 32));
      result = prime * result + (int) (snrQuality ^ (snrQuality >>> 32));
      result = prime * result
            + (int) (symbolErrorQuality ^ (symbolErrorQuality >>> 32));
      return result;
   }

   @Override
   public boolean equals( Object obj )
   {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      TunerStatus other = (TunerStatus) obj;
      if (channel == null)
      {
         if (other.channel != null) return false;
      }
      else if (!channel.equals(other.channel)) return false;
      if (lockStr == null)
      {
         if (other.lockStr != null) return false;
      }
      else if (!lockStr.equals(other.lockStr)) return false;
      if (lockSupported != other.lockSupported) return false;
      if (lockUnsupported != other.lockUnsupported) return false;
      if (packetsPerSecond != other.packetsPerSecond) return false;
      if (rawBitsPerSecond != other.rawBitsPerSecond) return false;
      if (returnStatus != other.returnStatus) return false;
      if (signalPresent != other.signalPresent) return false;
      if (signalStrength != other.signalStrength) return false;
      if (snrQuality != other.snrQuality) return false;
      if (symbolErrorQuality != other.symbolErrorQuality) return false;
      return true;
   }
}
