package com.zaren.HdhomerunSignalMeterLib.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class ErrorHandler
{
   public static Activity mainActivity;

   private static class HandleErrorRunnable implements Runnable
   {
      private String message;
      
      HandleErrorRunnable(String message)
      {
         this.message = message;
      }
      
      @Override
      public void run()
      {
         HandleErrorToast(message);
      }   
   }
   
   private static void HandleErrorToast(String message)
   {
      Toast toast = Toast.makeText(mainActivity, message,
            Toast.LENGTH_LONG);
      toast.show();
   }

   public static void HandleError(String message)
   {
      mainActivity.runOnUiThread(new HandleErrorRunnable(message));
   }
   
   public static void HandleError(Exception e)
   {
      mainActivity.runOnUiThread(new HandleErrorRunnable(e.getMessage()));
   }

   public static void HandleError(int returnValFromJNI, String message)
   {
      if (returnValFromJNI == 0)
      {
         mainActivity.runOnUiThread(new HandleErrorRunnable(message + " Command Rejected"));
      }
      else if (returnValFromJNI == -1)
      {
         mainActivity.runOnUiThread(new HandleErrorRunnable(message + " Communication Error"));
      }
   }
}
