/*
 * This file is part of MAME4droid.
 *
 * Copyright (C) 2011 David Valdeita (Seleuco)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * In addition, as a special exception, Seleuco
 * gives permission to link the code of this program with
 * the MAME library (or with modified versions of MAME that use the
 * same license as MAME), and distribute linked combinations including
 * the two.  You must obey the GNU General Public License in all
 * respects for all of the code used other than MAME.  If you modify
 * this file, you may extend this exception to your version of the
 * file, but you are not obligated to do so.  If you do not wish to
 * do so, delete this exception statement from your version.
 */

package com.seleuco.mame4all.views;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.seleuco.mame4all.Emulator;
import com.seleuco.mame4all.MAME4all;
import com.seleuco.mame4all.R;
import com.seleuco.mame4all.helpers.PrefsHelper;
import com.seleuco.mame4all.input.ControlCustomizer;
import com.seleuco.mame4all.input.InputHandler;
import com.seleuco.mame4all.input.InputValue;
import com.seleuco.mame4all.input.TiltSensor;

public class InputView extends ImageView {
		
	protected MAME4all mm = null;
	protected Bitmap bmp = null;
	protected Paint pnt = new Paint();
	protected Rect rsrc = new Rect();
	protected Rect rdst = new Rect();
	protected int   ax = 0;
	protected int   ay = 0;
	protected float dx = 1;
	protected float dy = 1;
	
	static BitmapDrawable stick_images[] = null;
	static BitmapDrawable btns_images[][] = null;
		  		 
	public void setMAME4all(MAME4all mm) {
		this.mm = mm;
		
		if(stick_images==null)
		{
			stick_images = new BitmapDrawable[9];
			stick_images[InputHandler.STICK_DOWN] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_down);
			stick_images[InputHandler.STICK_DOWN_LEFT] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_down_left);
			stick_images[InputHandler.STICK_DOWN_RIGHT] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_down_right);
			stick_images[InputHandler.STICK_LEFT] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_left);
			stick_images[InputHandler.STICK_NONE] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_none);
			stick_images[InputHandler.STICK_RIGHT] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_right);
			stick_images[InputHandler.STICK_UP] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_up);
			stick_images[InputHandler.STICK_UP_LEFT] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_up_left);
			stick_images[InputHandler.STICK_UP_RIGHT] = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.dpad_up_right);
		}
		
		if(btns_images==null)
		{
			btns_images = new BitmapDrawable[InputHandler.NUM_BUTTONS][2];
			btns_images[InputHandler.BTN_A][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_a);
			btns_images[InputHandler.BTN_A][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_a_press);
			
			btns_images[InputHandler.BTN_B][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_b);
			btns_images[InputHandler.BTN_B][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_b_press);
			
			btns_images[InputHandler.BTN_X][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_x);
			btns_images[InputHandler.BTN_X][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_x_press);
			
			btns_images[InputHandler.BTN_Y][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_y);
			btns_images[InputHandler.BTN_Y][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_y_press);
			
			btns_images[InputHandler.BTN_L1][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_l1);
			btns_images[InputHandler.BTN_L1][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_l1_press);
	
			btns_images[InputHandler.BTN_R1][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_r1);
			btns_images[InputHandler.BTN_R1][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_r1_press);
			
			btns_images[InputHandler.BTN_L2][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_l2);
			btns_images[InputHandler.BTN_L2][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_l2_press);
	
			btns_images[InputHandler.BTN_R2][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_r2);
			btns_images[InputHandler.BTN_R2][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_r2_press);
			
			btns_images[InputHandler.BTN_START][InputHandler.BTN_NO_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_start);
			btns_images[InputHandler.BTN_START][InputHandler.BTN_PRESS_STATE] 
			                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_start_press);
			
			btns_images[InputHandler.BTN_SELECT][InputHandler.BTN_NO_PRESS_STATE] 
				                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_select);
			btns_images[InputHandler.BTN_SELECT][InputHandler.BTN_PRESS_STATE] 
				                                 = (BitmapDrawable)mm.getResources().getDrawable(R.drawable.button_select_press);
		}
	}

	public InputView(Context context) {
		super(context);
		init();
	}

	public InputView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public InputView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	protected void init(){		
		pnt.setARGB(255, 255, 255, 255);
		//p.setTextSize(25);
	    pnt.setStyle(Style.STROKE);		
	    
		pnt.setARGB(255,255,255,255);
		pnt.setTextSize(16);
		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);		
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if(drawable!=null)
		{	
		    BitmapDrawable bmpdrw = (BitmapDrawable)drawable;
		    bmp = bmpdrw.getBitmap();
	    }
	    else
	    {
	    	bmp = null;
	    }
		
		super.setImageDrawable(drawable);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
         
		if(mm==null)
		{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		int widthSize  = 1;
		int heightSize = 1;
		
		if(mm.getMainHelper().getscrOrientation()==Configuration.ORIENTATION_LANDSCAPE)
		{
			widthSize = mm.getWindowManager().getDefaultDisplay().getWidth();
			heightSize = mm.getWindowManager().getDefaultDisplay().getHeight();
		}
		else
		{	
			int w = 1;//320;
			int h = 1;//240;
			
			if(mm!=null && mm.getInputHandler().getMainRect()!=null)
			{
				w = mm.getInputHandler().getMainRect().width();
				h = mm.getInputHandler().getMainRect().height();
			}
			
			if(w==0)w=1;
			if(h==0)h=1;

			float desiredAspect = (float) w / (float) h;
			
			widthSize = mm.getWindowManager().getDefaultDisplay().getWidth();
			heightSize = (int)(widthSize / desiredAspect);
		}		

		setMeasuredDimension(widthSize, heightSize);
	}
	
	public void updateImages(){
        ArrayList<InputValue> data = mm.getInputHandler().getAllInputData();
        
        if(data==null)return;
        
        for(int i=0; i<data.size();i++)
        {
        	InputValue v = data.get(i); 
        	if(v.getType()==InputHandler.TYPE_STICK_IMG)
        	{
        	   
        		for(int j=0; j< stick_images.length;j++)
        		{
        		  stick_images[j].setBounds(v.getRect());
        		  stick_images[j].setAlpha(mm.getInputHandler().getOpacity());
        		}
        	}
        	else if(v.getType()==InputHandler.TYPE_BUTTON_IMG)
        	{
        	    btns_images[v.getValue()][InputHandler.BTN_PRESS_STATE].setBounds(v.getRect());
        	    btns_images[v.getValue()][InputHandler.BTN_PRESS_STATE].setAlpha(mm.getInputHandler().getOpacity());
        	    btns_images[v.getValue()][InputHandler.BTN_NO_PRESS_STATE].setBounds(v.getRect());
        	    btns_images[v.getValue()][InputHandler.BTN_NO_PRESS_STATE].setAlpha(mm.getInputHandler().getOpacity());
        	}        	
        }			
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);
				
		int bw = 1;
		int bh = 1;
				
		if(mm!=null && mm.getInputHandler().getMainRect()!=null)
		{
			bw = mm.getInputHandler().getMainRect().width();
			bh = mm.getInputHandler().getMainRect().height();
		}
		
		if(bw==0)bw=1;
		if(bh==0)bh=1;
				
		float desiredAspect = (float) bw / (float) bh;
		
		int tmp = (int)((float) w / desiredAspect); 		
		if(tmp <= h)
		{	
			ax = 0;
			ay = (h - tmp) / 2;
			h = tmp;
		}	
		else
		{
			tmp = (int)((float) h * desiredAspect);
			ay = 0;
			ax = (w - tmp) / 2;
			w = tmp;
		}
					
		dx = (float) w / (float) bw;
		dy = (float) h / (float) bh;
		
		if(mm==null || mm.getInputHandler()==null)
			return;
		
		mm.getInputHandler().setFixFactor(ax,ay,dx,dy);
		
		updateImages();	
        
		//mm.getDialogHelper().setInfoMsg("w:"+w+"h:"+h);
		//mm.showDialog(DialogHelper.DIALOG_INFO);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		if(bmp != null)
		   super.onDraw(canvas);
		
		if(mm==null)return;
		
        ArrayList<InputValue> data = mm.getInputHandler().getAllInputData();
        for(int i=0; i<data.size();i++)
        {
        	InputValue v = data.get(i); 
        	BitmapDrawable d = null;
        	if(mm.getPrefsHelper().getControllerType() == PrefsHelper.PREF_DIGITAL && v.getType()==InputHandler.TYPE_STICK_IMG && canvas.getClipBounds().intersect(v.getRect()))
        	{
               //canvas.drawBitmap(stick_images[mm.getInputHandler().getStick_state()].getBitmap(), null, v.getRect(), null);
        	   d = stick_images[mm.getInputHandler().getStick_state()];
        	}
        	else if(mm.getPrefsHelper().getControllerType() != PrefsHelper.PREF_DIGITAL && v.getType()==InputHandler.TYPE_ANALOG_RECT && canvas.getClipBounds().intersect(v.getRect()) )
        	{
        		mm.getInputHandler().getAnalogStick().draw(canvas);
        	}        	
        	else if(v.getType()==InputHandler.TYPE_BUTTON_IMG && canvas.getClipBounds().intersect(v.getRect()) )
        	{
        	   //canvas.drawBitmap(btns_images[v.getValue()][mm.getInputHandler().getBtnStates()[v.getValue()]].getBitmap(), null, v.getRect(), null);
        	   if(mm.getMainHelper().getscrOrientation() == Configuration.ORIENTATION_LANDSCAPE)
        	   {
            	      int n = Emulator.getValue(Emulator.LAND_BUTTONS_KEY);   
            	      int b = v.getValue();
            	      if(!ControlCustomizer.isEnabled())
            	      { 	  
	        		      if(b==InputHandler.BTN_Y && n < 4)continue;
	        	          if(b==InputHandler.BTN_A && n < 3)continue;
	        	          if(b==InputHandler.BTN_X && n < 2)continue;
	        	          if(b==InputHandler.BTN_B && n < 1)continue;  
	        	                            
	        	          if(b==InputHandler.BTN_L1 && Emulator.getValue(Emulator.HIDE_LR__KEY)==1)continue;
	        	          if(b==InputHandler.BTN_R1 && Emulator.getValue(Emulator.HIDE_LR__KEY)==1)continue;
            	      }
        	   }
        	   d = btns_images[v.getValue()][mm.getInputHandler().getBtnStates()[v.getValue()]];
        	} 

        	
        	if(d!=null)
        	{
        		//d.setBounds(v.getRect());
        		d.draw(canvas);
        	}
        }
        
        if(ControlCustomizer.isEnabled())
           mm.getInputHandler().getControlCustomizer().draw(canvas);
        
        if(Emulator.isDebug())
        {
			ArrayList<InputValue> ids = mm.getInputHandler().getAllInputData();
			Paint p2 = new Paint();		
	   	    p2.setARGB(255, 255, 255, 255);
			p2.setStyle(Style.STROKE);
		
			for(int i=0; i<ids.size();i++)
			{
			   InputValue v = ids.get(i);
			   Rect r = v.getRect();
			   if(r!=null  )
			   {

			       if (v.getType()==InputHandler.TYPE_BUTTON_RECT)
			    	  canvas.drawRect(r, p2);
			       else if(mm.getPrefsHelper().getControllerType() == PrefsHelper.PREF_DIGITAL && v.getType()==InputHandler.TYPE_STICK_RECT)
			    	   canvas.drawRect(r, p2);
			       else if(mm.getPrefsHelper().getControllerType() != PrefsHelper.PREF_DIGITAL && v.getType()==InputHandler.TYPE_ANALOG_RECT)
			    	   canvas.drawRect(r, p2);
			   }  
			}
			
            p2.setTextSize(20);
            if(TiltSensor.isEnabled())
			   canvas.drawText(TiltSensor.str, 100, 100, p2);
        }	
	}	
}
