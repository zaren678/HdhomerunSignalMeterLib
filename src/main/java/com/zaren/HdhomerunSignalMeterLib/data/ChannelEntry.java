package com.zaren.HdhomerunSignalMeterLib.data;

public class ChannelEntry
{
   private String name;
   private int frequency;
   private int channelNumber;
   private ChannelEntry next;
   private ChannelEntry prev;
   /**
    * @param name
    * @param frequency
    * @param channelNumber
    */
   public ChannelEntry(String name, int frequency, int channelNumber)
   {
      super();
      this.name = name;
      this.frequency = frequency;
      this.channelNumber = channelNumber;
      this.next = null;
      this.prev = null;
   }
   /**
    * @return the next
    */
   public ChannelEntry getNext()
   {
      return next;
   }
   /**
    * @param next the next to set
    */
   public void setNext(ChannelEntry next)
   {
      this.next = next;
   }
   /**
    * @return the prev
    */
   public ChannelEntry getPrev()
   {
      return prev;
   }
   /**
    * @param prev the prev to set
    */
   public void setPrev(ChannelEntry prev)
   {
      this.prev = prev;
   }
   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }
   /**
    * @return the frequency
    */
   public int getFrequency()
   {
      return frequency;
   }
   /**
    * @return the channelNumber
    */
   public int getChannelNumber()
   {
      return channelNumber;
   }
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "ChannelEntry num=" + channelNumber + ", freq="
            + frequency + ", name=" + name;
   }
   
   
}
