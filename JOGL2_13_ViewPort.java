



import com.jogamp.opengl.GLAutoDrawable;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL4.*; 


public class JOGL2_13_ViewPort extends JOGL2_12_RobotSolar {
  
	  public void display(GLAutoDrawable glDrawable) {

		    cnt++;
		    depth = (cnt/100)%7;

		    gl.glClear(GL_COLOR_BUFFER_BIT|
		               GL_DEPTH_BUFFER_BIT);

		    if (cnt%150==0) {
		      dalpha = -dalpha;
		      dbeta = -dbeta;
		      dgama = -dgama;
		    }
		    alpha += dalpha;
		    beta += dbeta;
		    gama += dgama;		  
		  
		  if ((cnt % 1000) < 500) {
			  gl.glViewport(0, 0, WIDTH, HEIGHT);
		  }
		  else {
			  gl.glViewport(0, 0, cnt % WIDTH, cnt % HEIGHT);			  
		  }
		  drawRobot(O, A, B, C, alpha*dg, beta*dg, gama*dg);
	      cylinderm = cylinderm-cylinderD;
	      spherem = spherem-sphereD;
	      conem = conem-coneD;
		  
		
		  gl.glViewport(0, 0, WIDTH/2, HEIGHT/2);			  		  
		  drawRobot(O, A, B, C, alpha*dg, beta*dg, gama*dg);
	      cylinderm = cylinderm-cylinderD;
	      spherem = spherem-sphereD;
	      conem = conem-coneD;

		  gl.glViewport(WIDTH/2, HEIGHT/2, WIDTH/2, HEIGHT/2);			  		  
		  drawRobot(O, A, B, C, alpha*dg, beta*dg, gama*dg);
	      cylinderm = cylinderm-cylinderD;
	      spherem = spherem-sphereD;
	      conem = conem-coneD;

		  gl.glViewport(WIDTH/2, 0, WIDTH/2, HEIGHT/2);			  
		  drawRobot(O, A, B, C, alpha*dg, beta*dg, gama*dg);
	      cylinderm = cylinderm-cylinderD;
	      spherem = spherem-sphereD;
	      conem = conem-coneD;
		  
		  gl.glViewport(0, HEIGHT/2, WIDTH/2, HEIGHT/2);			  		  
		  drawRobot(O, A, B, C, alpha*dg, beta*dg, gama*dg);
	 }


	  
	  
	  public static void main(String[] args) {
	    new JOGL2_13_ViewPort();
	  }

}
