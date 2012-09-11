package com.zaren.HdhomerunSignalMeterLib.data;

public class HdhomerunDiscoverDevice
{
   public int ip_addr = 0;
   public long type = 0;
   public long id = 0;
   public int tuner_id = 0;
   private String mName;
   
   public HdhomerunDiscoverDevice(int ip_addr_val, long type_val, long id_val, int tuner_id_val)
   {
      this.ip_addr = ip_addr_val;
      this.type = type_val;
      this.id = id_val;
      this.tuner_id = tuner_id_val;
      mName = Long.toHexString(id) + "-" + tuner_id;
   }

   public String getName()
   {
      return mName;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return mName;
   }  
}
