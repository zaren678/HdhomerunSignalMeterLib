package com.zaren.HdhomerunSignalMeterLib.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

public class HDHomerunLogger
{

   public static final String TAG = "hdhomerunSignalMeter";
   //public static final int MinimumLogLevel = 2; // Verbose
   public static final int MinimumLogLevel = 3; // Debug
   //public static final int MinimumLogLevel = 4; // Info
   //public static final int MinimumLogLevel = 5; // Warn
   //public static final int MinimumLogLevel = 6; // Error

   private static String mTag = TAG;
   
   private static boolean logToFile = false;
   private static final long MAX_LOG_FILE_SIZE = 10000000; //10 MB

   // public static final int MinimumLogLevel = 7; // Assert

   public static void setTag( String aTag )
   {
      mTag = aTag;
   }
   
   public static void d(String message)
   {
      debug(mTag, message);
   }
   
   @SuppressWarnings("unused")
   public static void debug(String tag, String message)
   {
      if (android.util.Log.DEBUG < MinimumLogLevel) return;
      Log.d(tag, message);

      if (logToFile == true)
      {
         writeToLogFile(tag, message);
      }
   }

   public static void v(String message)
   {
      verbose(mTag,message);
   }
   
   @SuppressWarnings("unused")
   public static void verbose(String tag, String message)
   {
      if (android.util.Log.VERBOSE < MinimumLogLevel) return;
      Log.v(tag, message);

      if (logToFile == true)
      {
         writeToLogFile(tag, message);
      }
   }

   public static void i(String message)
   {
      info(mTag,message);
   }
   
   @SuppressWarnings("unused")
   public static void info(String tag, String message)
   {
      if (android.util.Log.INFO < MinimumLogLevel) return;
      Log.i(tag, message);

      if (logToFile == true)
      {
         writeToLogFile(tag, message);
      }
   }

   public static void w(String message)
   {
      warn(mTag,message);
   }
   
   @SuppressWarnings("unused")
   public static void warn(String tag, String message)
   {
      if (android.util.Log.WARN < MinimumLogLevel) return;
      Log.w(tag, message);

      if (logToFile == true)
      {
         writeToLogFile(tag, message);
      }
   }

   public static void e(String message)
   {
      error(mTag,message);
   }
   
   @SuppressWarnings("unused")
   public static void error(String tag, String message)
   {
      if (android.util.Log.ERROR < MinimumLogLevel) return;
      Log.e(tag, message);

      if (logToFile == true)
      {
         writeToLogFile(tag, message);
      }
   }

   public static String getLogFilePath()
   {
      return Environment.getExternalStorageDirectory() + "/" + "hdhomerun-signal-meter.log";
   }
   
   private synchronized static void writeToLogFile(String Tag, String message)
   {
      // Make the necessary directories if needed.
      // File dbDir = new File("/sdcard/hdhomerun-signal-meter");
      // if (!dbDir.exists())
      // {
      // dbDir.mkdirs();
      // }

      String state = Environment.getExternalStorageState();
      if (Environment.MEDIA_MOUNTED.equals(state))
      {
         //good case
      }
      else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
      {
         return;
      }
      else
      {
         return;
      }

      File root = Environment.getExternalStorageDirectory();
      if( !root.canWrite() )
      {
         return;
      }
      
      File logFile = new File( getLogFilePath() );
      if (!logFile.exists())
      {
         try
         {
            logFile.createNewFile();
         }
         catch (IOException e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(mTag, e.toString());
         }
      }
      
      try
      {
         //Format the log: Date Tag Message (In the future I might want to think about putting thread name in here)
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
               Locale.US);
         String text = sdf.format(Calendar.getInstance().getTime());

         text += " " + Tag + " : " + message;
         
         //Lets make sure the file isn't getting to big, if it is delete it and start over
         if(logFile.length() + text.length() > MAX_LOG_FILE_SIZE )
         {
            logFile.delete();
            
            try
            {
               logFile.createNewFile();
            }
            catch (IOException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
               Log.d(mTag, e.toString());
               return;
            }
         }
         
         
         // BufferedWriter for performance, true to set append to file flag
         BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
         buf.append(text);
         buf.newLine();
         buf.close();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public static void setDebugToFile(boolean debugToFile)
   {
      HDHomerunLogger.i("Debug to file set to " + debugToFile);
      
      if( debugToFile )
      {
         //if switching to true delete the old file
         File theLogFile = new File( getLogFilePath() );
         theLogFile.delete();
      }
      logToFile = debugToFile;
   }
}
