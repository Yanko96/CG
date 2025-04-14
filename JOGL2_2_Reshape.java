


import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_LINES;

import java.awt.event.*;
//import java.awt.event.MouseMotionListener;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;


public class JOGL2_2_Reshape extends JOGL2_1_Clock2d implements MouseMotionListener {
	
  // the point to be dragged is the lowerleft corner of the rectangle
  // corresponding to physical coordinates because mouse event receives physical coordinates
  static float P1[] = {-WIDTH/4, -HEIGHT/4, 0f};

  // reshape scale value
  float sx = 1, sy = 1;

  // when mouse is dragged, a new lowerleft point
  // and scale value for the rectangular area
  public void mouseDragged(MouseEvent e) {
	  
	   // original window size
	   float wd1 = WIDTH/2;
	    float ht1 = HEIGHT/2;

	    // The mouse location, new lowerleft corner
	    P1[0] = e.getX()-WIDTH/2;
	    P1[1] = HEIGHT/2-e.getY();
	    
	    float wd2 = WIDTH/4-P1[0];
	    float ht2 = HEIGHT/4-P1[1];
	    
	   // System.out.println(P1[0] + ", " + P1[1]);

	    // scale value of the current rectangular area
	    sx = wd2/wd1;
	    sy = ht2/ht1;
	    //System.out.println(sx + ", " + sy);
	    
	    // scale the mouse position to (-1, 1) along x and y as the default viewport logical coordinates
	    //P1[0] = 2*P1[0]/WIDTH; 
	    //P1[1] = 2*P1[1]/WIDTH; 
  }


  public void mouseMoved(MouseEvent e) {

 }


  public void init(GLAutoDrawable drawable) {

    super.init(drawable);
    
    // listen to mouse motioin
    canvas.addMouseMotionListener(this);
    //drawable.addMouseMotionListener(this);
  }


  public void display(GLAutoDrawable glDrawable) {
    // the rectangle lowerleft and upperright corners
	  // homogeneous coordinates
	   float c[] = {0.0f, 0.0f, 0.0f, 1f};
	   float h[] = {0.0f, 0.8f, 0.0f, 1f};
	   float color[] = {0, 0, 0}; 

	   long curTime;
	   float hAngle, hsecond, hminute, hhour;

	   float v0[] = {-1f/2f, -1f/2f, 0f};
	   float v1[] = {1f/2f, 1f/2f, 0f};

 
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
  
    
 
    // reshape according to the current scale
    myLoadIdentity();
    myTranslatef(2*P1[0]/WIDTH, 2*P1[1]/HEIGHT, 0f);
    myScalef(sx, sy, 1f);
    myTranslatef(-v0[0], -v0[1], 0f);
 
    float v00[] = {0, 0, 0};
    float v01[] = {0, 0, 0};
    float v11[] = {0, 0, 0};
    float v10[] = {0, 0, 0};
    
    v00[0] = v0[0];  v00[1] = v0[1]; 
    v01[0] = v1[0];  v01[1] = v0[0]; 
    v11[0] = v1[0];  v11[1] = v1[1]; 
    v10[0] = v0[0];  v10[1] = v1[1]; 
    
    //gl.glColor3f(1, 1, 1); // the rectangle is white
    color[0] = 1f;  color[1] = 1f;   color[2] = 1f; 
    uploadColor(color); // uniform iColor 
    transDrawClock(v00, v01);
    transDrawClock(v01, v11);
    transDrawClock(v11, v10);
    transDrawClock(v10, v00);
    
  /* // rectangle area
    float v00[] = new float[2], v11[] = new float[2];
    my2dTransformf(v0, v00);
    my2dTransformf(v1, v11);
    gl.glBegin(GL.GL_LINE_LOOP);
    gl.glVertex3f(v00[0], v00[1], 0);
    gl.glVertex3f(v11[0], v00[1], 0);
    gl.glVertex3f(v11[0], v11[1], 0);
    gl.glVertex3f(v00[0], v11[1], 0);
    gl.glEnd();
	*/
	    
	    curTime = System.currentTimeMillis()/1000;
	    // returns the current time in milliseconds

	    hsecond = curTime%60;
	    curTime = curTime/60;
	    hminute = curTime%60 + hsecond/60f;
	    curTime = curTime/60;
	    hhour = (curTime%12)+7+hminute/60f; // winter + 7; summer + 8
	    // Eastern Standard Time (daylight saving) 

	    //gl.glColor3f(1, 0, 0); // second hand in blue
	    color[0] = 0;  color[1] = 0;   color[2] = 1; 
	    uploadColor(color); // uniform iColor 
	    
	    // reshape according to the mouse position
	    myLoadIdentity();
	    myTranslatef(2*P1[0]/WIDTH, 2*P1[1]/HEIGHT, 0f);
	    myScalef(sx, sy, 1f);
	    myTranslatef(-v0[0], -v0[1], 0f);
	    
	    // rotate according to the clock hand length/time
	    hAngle = (float)Math.PI*hsecond/30f; // arc angle

	    myTranslatef(c[0], c[1], 0f);
	    myRotatef(-hAngle, 0f, 0f, 1f);
	    myTranslatef(-c[0], -c[1], 0f);
	    gl.glLineWidth(1);
	    transDrawClock(c, h);


	    // gl.glColor3f(0, 1, 0); // minute hand in green
	    color[0] = 0;  color[1] = 1;   color[2] = 0; 
	    uploadColor(color); // uniform iColor 

	    myLoadIdentity();
	    myTranslatef(2*P1[0]/WIDTH, 2*P1[1]/HEIGHT, 0f);
	    myScalef(sx, sy, 1f);
	    myTranslatef(-v0[0], -v0[1], 0f);

	    hAngle = (float)Math.PI*hminute/30; // arc angle
	    myTranslatef(c[0], c[1], 0f);
	    myScalef(0.8f, 0.8f, 1f); // minute hand shorter
	    myRotatef(-hAngle, 0f, 0f, 1f);
	    myTranslatef(-c[0], -c[1], 0f);
	    gl.glLineWidth(2);
	    transDrawClock(c, h);

	    //gl.glColor3f(0, 0, 1); // hour hand in red
	    color[0] = 1;  color[1] = 0;   color[2] = 0; 
	    uploadColor(color); // uniform iColor 

	    myLoadIdentity();
	    myTranslatef(2*P1[0]/WIDTH, 2*P1[1]/HEIGHT, 0f);
	    myScalef(sx, sy, 1f);
	    myTranslatef(-v0[0], -v0[1], 0f);

	    hAngle = (float)Math.PI*hhour/6; // arc angle
	    myTranslatef(c[0], c[1], 0f);
	    myScalef(0.5f, 0.5f, 1f); // hour hand shortest
	    myRotatef(-hAngle, 0f, 0f, 1f);
	    myTranslatef(-c[0], -c[1], 0f);
	    gl.glLineWidth(3);
	    transDrawClock(c, h);
	    
	  }


  public static void main(String[] args) {
    new JOGL2_2_Reshape();
  }

}
