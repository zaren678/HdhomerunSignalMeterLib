package com.zaren.HdhomerunSignalMeterLib.ui;

import android.content.Context;

import com.zaren.HdhomerunSignalMeterLib.data.DeviceController;

public interface HdhomerunSignalMeterUiInt
{

   public static final int DIALOG_CONNECT_TO_SERVER = 1;

   public abstract void setChannelEditText( String channel );

   public abstract void setCntrl( DeviceController deviceControl );

   /**
    * @return the cntrl
    */
   public abstract DeviceController getCntrl();
   
   public abstract Context getContext();

   public abstract void pause();

   public abstract void resume();

   public abstract void stop();

   /**
    * @return the enableDetailsMenu
    */
   public abstract boolean isEnableDetailsMenu();

   /*public abstract void startVideo( int videoInputPort, int videoOutputPort );

   public abstract Dialog onCreateDialog( int id );

   public abstract void setDialogMessage( int id, String message );

   public abstract void dismissDialog( int id );

   public abstract void setDialogProgress( int id, int progress );

   public abstract AlertDialog getAlertDialog( int id );*/

}