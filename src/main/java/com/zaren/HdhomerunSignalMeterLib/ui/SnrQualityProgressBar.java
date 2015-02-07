package com.zaren.HdhomerunSignalMeterLib.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class SnrQualityProgressBar extends TextProgressBar
{
   private ProgressBarDrawables mProgressBarDrawables;

   public SnrQualityProgressBar(Context aContext, AttributeSet aAttrs,
         int aDefStyle)
   {
      super(aContext, aAttrs, aDefStyle);
   }

   public SnrQualityProgressBar(Context aContext, AttributeSet aAttrs)
   {
      super(aContext, aAttrs);
   }

   public SnrQualityProgressBar(Context aContext)
   {
      super(aContext);
   }    

   public void setProgressBarDrawables( ProgressBarDrawables aProgressBarDrawables )
   {
      mProgressBarDrawables = aProgressBarDrawables;
   }

   @Override
   public synchronized void setProgress( int aProgress )
   {
      setText( aProgress + "" );
      
      Drawable theColor = getSnrQualColor( aProgress );
      
      if( theColor != null )
      {
         setProgressBarColor( theColor );
      }
      
      super.setProgress(aProgress);
   }
   
   private Drawable getSnrQualColor(int aSnrQuality)
   {
      if( mProgressBarDrawables == null )
      {
         return null;
      }
      
      if (aSnrQuality >= 70)
      {
         return mProgressBarDrawables.greenDrawable; // Green
      }
      if (aSnrQuality >= 50)
      {
         return mProgressBarDrawables.yellowDrawable; // Yellow;
      }

      return mProgressBarDrawables.redDrawable; // Red;
   }
}
