package com.zaren.HdhomerunSignalMeterLib.ui;

import com.zaren.HdhomerunSignalMeterLib.data.TunerStatus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class SigStrengthProgressBar extends TextProgressBar
{

   private ProgressBarDrawables mProgressBarDrawables;

   public SigStrengthProgressBar(Context aContext, AttributeSet aAttrs,
         int aDefStyle)
   {
      super(aContext, aAttrs, aDefStyle);
   }

   public SigStrengthProgressBar(Context aContext, AttributeSet aAttrs)
   {
      super(aContext, aAttrs);
   }

   public SigStrengthProgressBar(Context aContext)
   {
      super(aContext);
   }    

   public void setProgressBarDrawables( ProgressBarDrawables aProgressBarDrawables )
   {
      mProgressBarDrawables = aProgressBarDrawables;
   }

   public synchronized void setProgress( int progress, TunerStatus aDeviceTunerStatus )
   {
      setText( progress + "" );
      
      Drawable theColor = getSigStrColor( aDeviceTunerStatus );
      
      if( theColor != null )
      {
         setProgressBarColor( theColor );
      }
      
      super.setProgress(progress);
   }
   
   private Drawable getSigStrColor(TunerStatus deviceTunerStatus)
   {
      if( mProgressBarDrawables == null )
      {
         return null;
      }
      
      if (!deviceTunerStatus.lockSupported)
      {
         return mProgressBarDrawables.neutralDrawable; // neutral
      }

      long ss_yellow_min;
      long ss_green_min;
      if (getTunerStatusLockIsBcast(deviceTunerStatus))
      {
         ss_yellow_min = 50; /* -30dBmV */
         ss_green_min = 75; /* -15dBmV */
      }
      else
      {
         ss_yellow_min = 80; /* -12dBmV */
         ss_green_min = 90; /* -6dBmV */
      }

      if (deviceTunerStatus.signalStrength >= ss_green_min)
      {
         return mProgressBarDrawables.greenDrawable; // Green
      }
      if (deviceTunerStatus.signalStrength >= ss_yellow_min)
      {
         return mProgressBarDrawables.yellowDrawable; // Yellow
      }

      return mProgressBarDrawables.redDrawable; // Red
   }
   
   private boolean getTunerStatusLockIsBcast(TunerStatus deviceTunerStatus)
   {
      String lockStr = deviceTunerStatus.lockStr;
      if (lockStr.contains("8vsb"))
      {
         return true;
      }
      if (lockStr.startsWith("t8"))
      {
         return true;
      }
      if (lockStr.startsWith("t7"))
      {
         return true;
      }

      return lockStr.startsWith("t6");

   }
   
}
