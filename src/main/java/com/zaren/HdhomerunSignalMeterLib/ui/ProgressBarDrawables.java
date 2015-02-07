package com.zaren.HdhomerunSignalMeterLib.ui;

import com.zaren.HdhomerunSignalMeterLib.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class ProgressBarDrawables
{

   protected Drawable greenDrawable;
   protected Drawable yellowDrawable;
   protected Drawable redDrawable;
   protected Drawable neutralDrawable;

   /**
    * 
    */
   public ProgressBarDrawables(Context aContext)
   {
      greenDrawable = aContext.getResources().getDrawable(R.drawable.progressbargreen);
      yellowDrawable = aContext.getResources().getDrawable(R.drawable.progressbaryellow);
      redDrawable = aContext.getResources().getDrawable(R.drawable.progressbarred);
      neutralDrawable = aContext.getResources().getDrawable(R.drawable.progressbarneutral);
   }

}
