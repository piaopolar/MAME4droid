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


package com.seleuco.mame4all;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GLRenderer implements Renderer {
    
    protected int mTex = -1;
    protected int[] mtexBuf = new int[1];    
	private final int[] mCrop;

    private final int[] mTextureName;   
    protected ShortBuffer shortBuffer = null;
    
	private FloatBuffer mFVertexBuffer;
	private FloatBuffer mTexBuffer;
	private ShortBuffer mIndexBuffer;
    
    protected boolean textureInit = false;
    protected boolean force10 = false;
   
    protected boolean smooth = false;
    
	protected MAME4all mm = null;
    
	public void setMAME4all(MAME4all mm) {
		this.mm = mm;
	}
    
    public GLRenderer()
    {
        mTextureName = new int[1];
        mCrop = new int[4];
    }

	public void changedEmulatedSize(){
        Log.v("mm","changedEmulatedSize "+shortBuffer+" "+Emulator.getScreenBuffer());
        if(Emulator.getScreenBuffer()==null)return;
        shortBuffer = Emulator.getScreenBuffer().asShortBuffer(); 
        textureInit = false;
	}
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	
        Log.v("mm","onSurfaceCreated ");
        
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_TEXTURE_2D);
               
        gl.glDisable(GL10.GL_DITHER);
        gl.glDisable(GL10.GL_LIGHTING);
        gl.glDisable(GL10.GL_BLEND);
        gl.glDisable(GL10.GL_CULL_FACE);        
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glDisable(GL10.GL_MULTISAMPLE);
                	
		if(!(gl instanceof GL11Ext) || force10)
		{
           gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
           gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}  
        
        textureInit=false;
    }
       
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        Log.v("mm","sizeChanged: ==> new Viewport: ["+w+","+h+"]");

        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof (0f, w, h, 0f, -1f,1f); 
        
        gl.glFrontFace(GL10.GL_CCW);
        
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        
        textureInit=false;
    }
    
    protected boolean isSmooth(){
    	return Emulator.isFrameFiltering();
    }
    
    protected int loadTexture(final GL10 gl) {

        int textureName = -1;
        if (gl != null) {
            gl.glGenTextures(1, mTextureName, 0);

            textureName = mTextureName[0];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);
            
            smooth = isSmooth();
            	
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                  smooth ? GL10.GL_LINEAR : GL10.GL_NEAREST);
                  
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_CLAMP_TO_EDGE);

            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                    GL10.GL_REPLACE);
            
            final int error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("SpriteMethodTest", "Texture Load GLError: " + error);
            }
        }
        return textureName;
    }
    
	public void initVertexes(GL10 gl) {
		
		if(gl instanceof GL11Ext && !force10)
			return;
		
		int width = Emulator.getEmulatedWidth();
		int height = Emulator.getEmulatedHeight();

		ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(4 * 2 * 4);
		tbb.order(ByteOrder.nativeOrder());
		mTexBuffer = tbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(4 * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		
		float scaleX = (float) Emulator.getWindow_width()/Emulator.getEmulatedWidth();
		float scaleY = (float) Emulator.getWindow_height()/Emulator.getEmulatedHeight();
		
		float[] coords = {
				// X, Y, Z				
				(int) ((float) width * scaleX), 0, 0,
				(int) ((float) width * scaleX),(int) ((float) height * scaleY), 0, 
				0, 0, 0, 
				0,(int) ((float) height * scaleY), 0 };
	    
        int width_p2  = Emulator.getEmulatedWidth() > 512 ? 1024 : 512;
        int height_p2 = 512;
        	
		// Texture coords
		float[] texturCoords = new float[] {

		1f / ((float) width_p2 / width), 0f, 0,
				1f / ((float) width_p2 / width),
				1f / ((float) height_p2 / height), 0, 0f, 0f, 0, 0f,
				1f / ((float) height_p2 / height), 0 };

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				mFVertexBuffer.put(coords[i * 3 + j]);
			}
		}

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				mTexBuffer.put(texturCoords[i * 3 + j]);
			}
		}

		for (int i = 0; i < 4; i++) {
			mIndexBuffer.put((short) i);
		}

		mFVertexBuffer.position(0);
		mTexBuffer.position(0);
		mIndexBuffer.position(0);
	}
    
	private void releaseTexture(GL10 gl) {
		if (mTex != -1) {
			gl.glDeleteTextures(1, new int[] { mTex }, 0);
		}		
	}
	
	public void dispose(GL10 gl) {
		releaseTexture(gl);
	}
    
    public void onDrawFrame(GL10 gl) {
       // Log.v("mm","onDrawFrame called "+shortBuffer);
    			
    	if(shortBuffer==null){
    		ByteBuffer buf = Emulator.getScreenBuffer();
    		if(buf==null)return;
            shortBuffer = buf.asShortBuffer();
    	}
    	
    	if(mTex==-1 || smooth!=isSmooth()) 
    		mTex = loadTexture(gl);  
    	
        gl.glActiveTexture(mTex);
        gl.glClientActiveTexture(mTex);

        shortBuffer.rewind();

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTex);
        
        if(!textureInit)
        {
        	initVertexes(gl);
        	
        	gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0,  GL10.GL_RGB,
                Emulator.getEmulatedWidth() > 512 ? 1024 : 512,512, 0,  GL10.GL_RGB,
                GL10.GL_UNSIGNED_SHORT_5_6_5 , shortBuffer);
            textureInit = true;
        }
       
        /*
    	gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0,  GL10.GL_RGB,
    			 Emulator.getEmulatedWidth(),Emulator.getEmulatedHeight(), 0,  GL10.GL_RGB,
                GL10.GL_UNSIGNED_SHORT_5_6_5, shortBuffer);
        */
        
        int width = Emulator.getEmulatedWidth();
        int height = Emulator.getEmulatedHeight();
                
		gl.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, GL10.GL_RGB, GL10.GL_UNSIGNED_SHORT_5_6_5, shortBuffer);
        
		if((gl instanceof GL11Ext) && !force10)
		{
	        mCrop[0] = 0; // u
	        mCrop[1] = height; // v
	        mCrop[2] = width; // w
	        mCrop[3] = -height; // h
	        
	        ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCrop, 0);
	                               
	        ((GL11Ext) gl).glDrawTexiOES(0, 0, 0, Emulator.getWindow_width()/*+1*/,Emulator.getWindow_height()/*+1*/);
		}
		else
		{	
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
					GL10.GL_UNSIGNED_SHORT, mIndexBuffer);					
		}		
    }
}