package ykong7_CS551;


/*
 * Created on Feb 2020
 * @author Jim X. Chen: transformation: Examples
 * 
 */



import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;


public class JOGL2_9_Solar extends JOGL2_8_Robot3d {
  
	  public void display(GLAutoDrawable glDrawable) {

	    depth = (cnt/100)%7;
	    cnt++;

	    gl.glClear(GL.GL_COLOR_BUFFER_BIT|
	               GL.GL_DEPTH_BUFFER_BIT);

	    myRotatef(cnt*dg/1000f, 1f, 0f, 0f); 
	    drawSolar(WIDTH/4, 2*cnt*dg, WIDTH/6, cnt*dg);
	  }


	  // notice that draw drawColorCoord didn't load the MV matrix, so it has to be following drawSphere(), which loads MV matrix
	  public void drawColorCoord(float xlen, float ylen,
	                             float zlen) {
	    GLUT glut = new GLUT();

	   // if (coordOff) return; // cjx for images

	    float vColor[] = {1, 0, 0}; 
	    float vPoint[] = {0, 0, 0, 0, 0, 0}; 
	    
	    vPoint[3] = xlen; 
	    drawLineJOGL(vPoint, vColor); 
	    vColor[0] = 0; vColor[1] = 1; 
	    vPoint[3] = 0; 
	    vPoint[4] = ylen; 
	    drawLineJOGL(vPoint, vColor); 
	    vColor[1] = 0; vColor[2] = 1; 
	    vPoint[4] = 0; 
	    vPoint[5] = zlen; 
	    drawLineJOGL(vPoint, vColor); 
	    
	    

	    /* glut doesn't work with vertex shader as far as I know */
	    

	    /*
	     * // coordinate labels: X, Y, Z
	     
	    myPushMatrix();
	    myTranslatef(xlen, 0, 0);
	    myScalef(xlen/WIDTH, xlen/WIDTH, 1);
	    glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, 'X');
	    myPopMatrix();

	    myPushMatrix();
	   // myColor3f(0, 1, 0);
	    myTranslatef(0, ylen, 0);
	    myScalef(ylen/WIDTH, ylen/WIDTH, 1);
	    glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, 'Y');
	    myPopMatrix();

	    myPushMatrix();
	    //myColor3f(1, 0, 0);
	    myTranslatef(0, 0, zlen);
	    myScalef(zlen/WIDTH, zlen/WIDTH, 1);
	    glut.glutStrokeCharacter(GLUT.STROKE_ROMAN, 'Z');
	    myPopMatrix();
	    */ 
	    
	  }

	  
	  void drawSolar(float E, float e, float M, float m) {

		    myPushMatrix();
		    
			    myPushMatrix();
				    myScalef(WIDTH/15f, WIDTH/15f, WIDTH/15f);
				    // sun
				    drawSphere();
				    gl.glLineWidth(4);
					drawColorCoord(4f, 4f, 4f);
			    myPopMatrix();
	
	
			   myRotatef(e, 0.0f, 1.0f, 0.0f);
			    // rotating around the "sun"; proceed angle
			    myTranslatef(E, 0.0f, 0.0f);
	
			    myPushMatrix();
				    myScalef(WIDTH/20f, WIDTH/20f, WIDTH/20f);
				    myRotatef(m*10, 0.0f, 1.0f, 0.0f); //earth's self rotation
				    drawSphere(); // earth 
				    gl.glLineWidth(2);
				    drawColorCoord(4f, 4f, 4f);
			    myPopMatrix();
	
			    myRotatef(m, 0.0f, 1.0f, 0.0f);
			    // rotating around the "earth"
			    myTranslatef(M, 0.0f, 0.0f);
			    myScalef(WIDTH/40f, WIDTH/40f, WIDTH/40f);
			    drawSphere();
			    gl.glLineWidth(1);
			    drawColorCoord(3f, 3f, 3f);

		    myPopMatrix();
		
		  }

	  
	  
  public static void main(String[] args) {
    new JOGL2_9_Solar();
  }

}
