



import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;


public class JOGL2_10_GenSolar extends JOGL2_9_Solar {
  	  
	  void drawSolar(float earthDistance,
	                 float earthAngle,
	                 float moonDistance,
	                 float moonAngle) {

		float tiltAngle = 45*dg;


	    // Global coordinates
		myPushMatrix(); 
		  myScalef(WIDTH/30, WIDTH/30, WIDTH/30);
		  drawSphere(); // a tiny sphere, for loading the matrix for drawing the coordinates
		gl.glLineWidth(4);
	    drawColorCoord(10, 10, 10);
		myPopMatrix(); 

	    myPushMatrix();
	    
	    myRotatef(earthAngle, 0.0f, 1.0f, 0.0f);
	    // rotating around the "sun"; proceed angle
	    myRotatef(tiltAngle, 0.0f, 0.0f, 1.0f);
	    myTranslatef(0.0f, earthDistance, 0.0f);
	    // cjx gl.glLineWidth(3);
	    myPushMatrix();
	    myScalef(WIDTH/20, WIDTH/20, WIDTH/20);
	    drawSphere();
	    gl.glLineWidth(3);
	    drawColorCoord(3, 5, 3);
	    myPopMatrix();

	    myRotatef(moonAngle, 0.0f, 1.0f, 0.0f);
	    // rotating around the "earth"
	    myTranslatef(moonDistance, 0.0f, 0.0f);
	    gl.glLineWidth(1);
	    myScalef(WIDTH/40, WIDTH/40, WIDTH/40);
	    drawSphere();
	    drawColorCoord(2, 2, 2);
	    myPopMatrix();
	  }
	  
	  
	  public static void main(String[] args) {
	    new JOGL2_10_GenSolar();
	  }

}
