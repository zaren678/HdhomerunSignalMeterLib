package com.zaren.HdhomerunSignalMeterLib.data;

public class CurrentChannelAndProgram
{
   private ProgramsList mPrograms;
   private int mProgramNum;
   private String mChannel;

   public CurrentChannelAndProgram( CurrentChannelAndProgram aOther )
   {
      if( aOther != null )
      {
         mPrograms = aOther.getPrograms();
         mProgramNum = aOther.getProgramNum();
         mChannel = aOther.getChannel();
      }
   }
   
   public CurrentChannelAndProgram(ProgramsList aProgramList, int aProgramNum,
         String aChannel)
   {
      mPrograms = aProgramList;
      mProgramNum = aProgramNum;
      mChannel = aChannel;
   }

   public CurrentChannelAndProgram()
   {
   }

   public ProgramsList getPrograms()
   {
      return mPrograms;
   }
      
   public void setPrograms( ProgramsList aPrograms )
   {
      mPrograms = aPrograms;
   }
   
   public int getProgramNum()
   {
      return mProgramNum;
   }
   
   public void setProgramNum( int aProgramNum )
   {
      mProgramNum = aProgramNum;
   }

   public String getChannel()
   {
      return mChannel;
   }
   
   public void setChannel( String aChannel )
   {
      mChannel = aChannel;
   }
            
}
