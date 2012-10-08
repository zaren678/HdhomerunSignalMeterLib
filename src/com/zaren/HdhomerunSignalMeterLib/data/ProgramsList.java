package com.zaren.HdhomerunSignalMeterLib.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.util.SparseArray;

public class ProgramsList extends SparseArray< ChannelScanProgram > implements Iterable< ChannelScanProgram >
{
   @Override
   public Iterator<ChannelScanProgram> iterator()
   {
      return values().iterator();
   }

   private Collection<ChannelScanProgram> values()
   {
      Collection< ChannelScanProgram > thePrograms = new ArrayList<ChannelScanProgram>();
      
      for( int i = 0; i < size(); i++ )
      {
         thePrograms.add( valueAt( i ) );
      }
      
      return thePrograms;
   }
   
   public List< ChannelScanProgram > toList()
   {
      ArrayList< ChannelScanProgram > thePrograms = new ArrayList<ChannelScanProgram>( values() ); 
      return thePrograms;
   }
}
