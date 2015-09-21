package com.zaren.HdhomerunSignalMeterLib.data;

import timber.log.Timber;

public class ChannelList
{
   private ChannelEntry head;
   private ChannelEntry tail;
   private int maxNumber = 0;
   private int minNumber = 1000000;
   
   /**
    * 
    */
   public ChannelList()
   {
      // TODO Auto-generated constructor stub
   }
   /**
    * @return the head
    */
   public ChannelEntry getHead()
   {
      return head;
   }
   /**
    * @param head the head to set
    */
   public void setHead(ChannelEntry head)
   {
      this.head = head;
   }
   /**
    * @return the tail
    */
   public ChannelEntry getTail()
   {
      return tail;
   }
   /**
    * @param tail the tail to set
    */
   public void setTail(ChannelEntry tail)
   {
      this.tail = tail;
   }
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      String retString = "ChannelList:\n";
      
      ChannelEntry entry = getHead();
      while(entry != null)
      {
         retString += entry.toString() + "\n";
         entry = entry.getNext();
      }
      
      return retString;
   }
   /**
    * @return the maxNumber
    */
   public int getMaxNumber()
   {
      return maxNumber;
   }
   /**
    * @return the minNumber
    */
   public int getMinNumber()
   {
      return minNumber;
   }
   
   public void findMaxAndMin()
   {
      //reset the min and max to get the new values
      maxNumber = 0;
      minNumber = 1000000;
      
      ChannelEntry entry = getHead();
      while(entry != null)
      {
         if(entry.getChannelNumber() > maxNumber) 
         {
            maxNumber = entry.getChannelNumber();
         }
         if(entry.getChannelNumber() < minNumber) 
         {
            minNumber = entry.getChannelNumber();
         }
         entry = entry.getNext();
      }
      
      Timber.d( "findMaxAndMin(): min " + minNumber + " max " + maxNumber );
   }
   
   public int frequencyToNumber(int frequency)
   {
      ChannelEntry entry = getHead();
      int retNumber = 0;
      while(entry != null)
      {
         if(entry.getFrequency() == frequency)
         {
            retNumber = entry.getChannelNumber();
            break;
         }
         entry = entry.getNext();
      }

      Timber.v("frequencyToNumber(): freq " + frequency + " num " + retNumber);
      
      return retNumber;
   }
   
}
