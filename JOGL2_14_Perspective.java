



import com.jogamp.opengl.GLAutoDrawable;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL4.*;


public class JOGL2_14_Perspective extends JOGL2_13_TravelSolar {
  
	public void myPerspective(double fovy, double aspect, double near, double far) {
		double left, right, bottom, top;

		fovy = fovy*dg; // convert degree to arc
		
		top = near*Math.tan(fovy/2);
		bottom = -top;
		right = aspect*top;
		left = -right;
		
		myFrustum((float) left, (float)right, (float)bottom, (float)top, (float)near, (float)far);
	}

	public void reshape(
		      GLAutoDrawable glDrawable, int x, int y, int w, int h) {

		    WIDTH = w; HEIGHT = h;
		 
			System.out.println("b) reshape: JOGL12_14_Perspective; gluPerspective style viewing volume"); 

			// enable zbuffer for hidden-surface removal
		    gl.glEnable(GL_DEPTH_TEST);

		    // specify the drawing area within the frame window
		    gl.glViewport(0, 0, w, h);

		    // projection is carried on the projection matrix
		    //gl.glMatrixMode(GL.GL_PROJECTION);
		    myLoadIdentity();
		    
		    myPerspective(40, w/h, w/3, 4*w);

		    // transformations are on the modelview matrix
		    //gl.glMatrixMode(GL_MODELVIEW);
		    myLoadIdentity();
		    myTranslatef(0, 0, -2.5f*w); 
	}

	  
	  public static void main(String[] args) {
	    new JOGL2_14_Perspective();
	  }

}
