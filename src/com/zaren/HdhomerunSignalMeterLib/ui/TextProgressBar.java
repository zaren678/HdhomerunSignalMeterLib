package com.zaren.HdhomerunSignalMeterLib.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TextProgressBar extends ProgressBar 
{
	private String text;  
    private Paint textPaint;
    private Rect bounds;
    
	public TextProgressBar(Context context) 
	{
		super(context);
		text = "0";  
        textPaint = new Paint();  
        textPaint.setColor(Color.BLACK);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bounds = new Rect();
	}

	public TextProgressBar(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		text = "0";  
        textPaint = new Paint();  
        textPaint.setColor(Color.BLACK);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bounds = new Rect();
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		text = "0";  
        textPaint = new Paint();  
        textPaint.setColor(Color.BLACK);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        bounds = new Rect();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		
        textPaint.getTextBounds(text, 0, text.length(), bounds);  
        int x = getWidth() / 2 - bounds.centerX();  
        int y = getHeight() / 2 - bounds.centerY();  
        canvas.drawText(text, x, y, textPaint);  
	}

	public synchronized void setText(String text) 
	{  
        this.text = text;  
        drawableStateChanged();  
    }  
  
    public void setTextColor(int color) 
    {  
        textPaint.setColor(color);  
        drawableStateChanged();  
    }
    
    protected void setProgressBarColor( Drawable aProgBarDrawable ) 
    {
       Rect bounds = getProgressDrawable().getBounds();
       setProgressDrawable(aProgBarDrawable);
       getProgressDrawable().setBounds(bounds);
    }
    
}
