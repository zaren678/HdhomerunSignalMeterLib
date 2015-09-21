package com.zaren.HdhomerunSignalMeterLib.data;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import timber.log.Timber;

public class OnChannelMapSelectedListener implements OnItemSelectedListener 
{
   private DeviceController ctrl;

	public OnChannelMapSelectedListener(DeviceController c)
	{
		ctrl = c;
	}

	public void onItemSelected(AdapterView<?> parent,
			                   View view,
			                   int pos,
			                   long id) throws IllegalAccessError
	{
	   String channelMapSelected;
	   
	   Timber.d( "OnChannelMapSelectedListener: pos " + pos );
	   
		channelMapSelected = (String)parent.getItemAtPosition(pos);
		ctrl.setChannelMap( channelMapSelected );
	
	}

	public void onNothingSelected(AdapterView<?> parent) 
	{
		// TODO Auto-generated method stub

	}

}
