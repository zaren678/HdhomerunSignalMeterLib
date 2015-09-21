package com.zaren.HdhomerunSignalMeterLib.ui;

import com.zaren.HdhomerunSignalMeterLib.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import timber.log.Timber;

public class ArraySpinner extends Spinner implements OnItemSelectedListener
{
   private ArrayAdapter<?> arrayAdapter;
   private OnItemSelectedListener listener;
   private int mPrevSelectedItemPos;
   private boolean ignoreFirst;
   
   public ArraySpinner(Context context)
   {
      super(context);
      init();
   }
   
   private void init()
   {
      //assume don't ignore first
      mPrevSelectedItemPos = -1;
   }

   public void setArrayAdapter(ArrayAdapter<?> adapter)
   {
      Timber.d( "ArraySpinner: setArrayAdapter" );
      this.setAdapter(adapter);
      arrayAdapter = adapter;
      
      if(ignoreFirst == false)
      {
         mPrevSelectedItemPos = -1;
      }
      else
      {
         mPrevSelectedItemPos = 0;
      }
   }
   
   public ArrayAdapter<?> getArrayAdapter()
   {
      return arrayAdapter;
   }

   /**
    * @param context
    * @param attrs
    * @param defStyle
    * @param ignoreFirst 
    */
   public ArraySpinner(Context context, AttributeSet attrs, int defStyle)
   {
      super(context, attrs, defStyle);
      init(attrs, context);
   }

   private void init(AttributeSet attrs, Context context)
   {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArraySpinner);
      
      ignoreFirst = a.getBoolean(R.styleable.ArraySpinner_ignoreFirst,false);
      
      Timber.d("ArraySpinner: init: ignoreFirst " +ignoreFirst);
      
      if(ignoreFirst == false)
      {
         mPrevSelectedItemPos = -1;
      }
      else
      {
         mPrevSelectedItemPos = 0;
      }
   }

   /**
    * @param context
    * @param attrs
    * @param ignoreFirst 
    */
   public ArraySpinner(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      init(attrs, context);
   }

   /* (non-Javadoc)
    * @see android.widget.AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
    */
   @Override
   public void setOnItemSelectedListener(OnItemSelectedListener _listener)
   {
      listener = _listener;
      super.setOnItemSelectedListener(this);
   }

   @Override
   public void onItemSelected(AdapterView<?> parent, View view, int pos,
         long id)
   {
      Timber.d("ArraySpinner: OnItemSelected:  pos: " + pos);
      if(mPrevSelectedItemPos == pos)
      {
         Timber.d("ArraySpinner: Ignore");
      }
      else
      {
         listener.onItemSelected(parent, view, pos, id);
      }

      mPrevSelectedItemPos = pos;
   }

   @Override
   public void onNothingSelected(AdapterView<?> parent)
   {
      listener.onNothingSelected(parent);
   }

   /**
    * This function sets the spinner's position without calling
    * the onItemSelected function of the onItemSelectedListener object
    * @param pos The position to set the spinner to
    */
   public void setSelectionSilently(int pos)
   {
      Timber.d("ArraySpinner: setSelectionSilently: pos " + pos);
      mPrevSelectedItemPos = pos;
      setSelection(pos);
   }
   
   public int getPrevSelectedItemPos()
   {
      Timber.d("ArraySpinner: getPrevSelectedItemPos: prevPos " + mPrevSelectedItemPos);
      return mPrevSelectedItemPos ;
   }

   /* (non-Javadoc)
    * @see android.widget.AbsSpinner#setSelection(int)
    */
   @Override
   public void setSelection(int position)
   {
      Timber.d("ArraySpinner: setSelection: pos " + position + " prevPos " + mPrevSelectedItemPos);
      super.setSelection(position);
   }
}
