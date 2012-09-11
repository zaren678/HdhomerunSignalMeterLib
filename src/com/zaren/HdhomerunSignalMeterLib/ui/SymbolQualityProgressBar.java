package com.zaren.HdhomerunSignalMeterLib.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class SymbolQualityProgressBar extends TextProgressBar
{
   private ProgressBarDrawables mProgressBarDrawables;

   public SymbolQualityProgressBar(Context aContext, AttributeSet aAttrs,
         int aDefStyle)
   {
      super(aContext, aAttrs, aDefStyle);
   }

   public SymbolQualityProgressBar(Context aContext, AttributeSet aAttrs)
   {
      super(aContext, aAttrs);
   }

   public SymbolQualityProgressBar(Context aContext)
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
      
      Drawable theColor = getSymQualColor( aProgress );
      
      if( theColor != null )
      {
         setProgressBarColor( theColor );
      }
      
      super.setProgress(aProgress);
   }
   
   private Drawable getSymQualColor( int aSymbolErrorQuality )
   {
      if( mProgressBarDrawables == null )
      {
         return null;
      }
      
      if (aSymbolErrorQuality >= 100)
      {
         return mProgressBarDrawables.greenDrawable; // Green
      }

      return mProgressBarDrawables.redDrawable; // Red
   }
}
