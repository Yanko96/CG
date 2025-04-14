


import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_LINES;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;

public class JOGL2_1_Clock2d extends JOGL2_0_2DTransform {

	  public void display(GLAutoDrawable glDrawable) {
		  // homogeneous coordinates
		   float c[] = {0.0f, 0.0f, 0.0f, 1f};
		   float h[] = {0.0f, 0.8f, 0.0f, 1f};
		   float color[] = {0, 0, 0};

		   long curTime;
		   float hAngle, hsecond, hminute, hhour;
		   
		   gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	    
		   curTime = System.currentTimeMillis()/1000;
		   // returns the current time in milliseconds

		   hsecond = curTime%60;
		   curTime = curTime/60;
		   hminute = curTime%60 + hsecond/60f;
		   curTime = curTime/60;
		   hhour = (curTime%12)+7+hminute/60f; // winter + 7; summer + 8
		   // Eastern Standard Time (daylight saving) 

		   hAngle = (float) Math.PI*hsecond/30f; // arc angle

			color[0] = 0.3f;  color[1] = 0.3f;   color[2] = 0.3f; 
			uploadColor(color); // send to the shader: iColor
		    drawCircle(WIDTH/5,  4); 
		   
			color[0] = 0;  color[1] = 0;   color[2] = 1; 
			uploadColor(color); // send to the shader: iColor
		    myLoadIdentity();
		    myTranslatef(c[0], c[1], 0f);
		    myRotatef(-hAngle, 0f, 0f, 1f);
		    myTranslatef(-c[0], -c[1], 0f);
		    gl.glLineWidth(10);
		    transDrawClock(c, h);

		    // gl.glColor3f(0, 1, 0); // minute hand in green
	    color[0] = 0;  color[1] = 1;   color[2] = 0; 
		uploadColor(color); // send to the shader: iColor
		
	    myLoadIdentity();
	    hAngle = (float) Math.PI*hminute/30; // arc angle
	    myTranslatef(c[0], c[1], 0f);
	    myScalef(0.8f, 0.8f, 1f); // minute hand shorter
	    myRotatef(-hAngle, 0f, 0f, 1f);
	    myTranslatef(-c[0], -c[1], 0f);
	    gl.glLineWidth(3);
	    transDrawClock(c, h);

	    //gl.glColor3f(0, 0, 1); // hour hand in red
	    color[0] = 1;  color[1] = 0;   color[2] = 0; 
	    uploadColor(color); 
	    
	    myLoadIdentity();
	    hAngle = (float) Math.PI*hhour/6; // arc angle
	    myTranslatef(c[0], c[1], 0f);
	    myScalef(0.5f, 0.5f, 1f); // hour hand shortest
	    myRotatef(-hAngle, 0f, 0f, 1f);
	    myTranslatef(-c[0], -c[1], 0f);
	    gl.glLineWidth(5);
	    transDrawClock(c, h);
	    
	  }


	  public void transDrawClock(float C[], float H[]) {
	 
		// prepare Modelview matrix to be sent to the vertex shader as uniform
		uploadMV(); 	
		
		// stores the two vertices to be sent to the vertex shader 
		float v[] = new float[6]; 
	    for (int i=0; i<3; i++) v[i] = C[i]; 
	    for (int i=0; i<3; i++) v[i+3] = H[i]; 
	
		// load vbo[0] with vertex data
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]); // use handle 0 		
		FloatBuffer vBuf = Buffers.newDirectFloatBuffer(v);
		gl.glBufferData(GL_ARRAY_BUFFER, vBuf.limit()*Float.BYTES,  //# of float * size of floats in bytes
				vBuf, // the vertex positions
				GL_STATIC_DRAW); // the data is static
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // associate vbo[0] with active vao buffer
		
			
		// draw a clock hand 
	    gl.glDrawArrays(GL_LINES, 0, 2); 
	  }




  public static void main(String[] args) {
    new JOGL2_1_Clock2d();
  }

}
