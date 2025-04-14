


import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_POINTS;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import static com.jogamp.opengl.GL4.*;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLAutoDrawable;

public class JOGL3_13_SrayTracing extends JOGL3_12_rayTracing {

	public void display(GLAutoDrawable glDrawable) {
		float[] viewpt = new float[3], raypt = new float[3];
		// initial ray: viewpt -> raypt

		float[] color = {0, 0, 0}; // traced color
		float[] icolor = {0, 0, 0}; // traced color

		ns = (int) (20*Math.random()); // random number of spheres
		nl = (int) (20*Math.random()); // random number of light sources
		
		// initialize 'ns' number of spheres
		for (int i = 0; i < ns; i++) {
			sphere[i][0] = 5 + (float) (Math.random() * WIDTH)/8; // sphere radius
			for (int j = 1; j < 4; j++) { //sphere center
				sphere[i][j] = -WIDTH/2 +(float) (Math.random() * WIDTH); 
			}
		}

		// initialize 'nl' light source locations
		for (int i = 0; i < nl; i++) {
			for (int j = 0; j < 3; j++) { // light source positions
				lightSrc[i][j] = -40*WIDTH + (float) (80*Math.random()*WIDTH); 
				lightClr[i][j] = (float)((Math.random() * WIDTH)/WIDTH); 
			}
		}

		// starting viewpoint on positive z axis
		viewpt[0] = 0;
		viewpt[1] = 0;
		viewpt[2] = 1.5f*HEIGHT;

	    gl.glViewport(WIDTH, 0, WIDTH, HEIGHT);

		// second pass using stochastic raytracing 
		for (float y = -HEIGHT / 2; y < HEIGHT / 2; y++) {
			for (float x = -WIDTH / 2; x < WIDTH / 2; x++) {

				depth = 5; 
				// stochastic raytracing: firing multiple rays stockastically
				for (int i=0; i<MAX; i++) { 
					raypt[0] = x - 0.5f + (float) (Math.random());
					raypt[1] = y - 0.5f + (float) (Math.random());
					raypt[2] = 0;
					rayTracing(icolor, viewpt, raypt, depth);

					for (int j=0; j<3; j++) {
						color[j] = color[j] + icolor[j]; 
					}
				}
				for (int j=0; j<3; j++) {
					color[j] = color[j]/MAX; 
				}

				// ray from viewpoint to a pixel on the screen
				raypt[0] = x;
				raypt[1] = y;
				raypt[2] = 0;
				// send vertex data to vertex shader through uniform
		 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(raypt);
				int colorLoc = gl.glGetUniformLocation(vfPrograms,  "rPoint"); 
				gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
				
				// send color data to fragment shader through uniform
		 		 cBuf = Buffers.newDirectFloatBuffer(color);
				 colorLoc = gl.glGetUniformLocation(vfPrograms,  "rColor"); 
				gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
				
				//draw the point
				gl.glDrawArrays(GL_POINTS, 0, 1);	
			}
		}
		
	    gl.glViewport(0, 0, WIDTH, HEIGHT);

		// trace rays against the spheres and a plane
		for (float y = -HEIGHT / 2; y < HEIGHT / 2; y++) {
			for (float x = -WIDTH / 2; x < WIDTH / 2; x++) {

				// ray from viewpoint to a pixel on the screen
				raypt[0] = x;
				raypt[1] = y;
				raypt[2] = 0;

				// tracing the ray (viewpt to raypt) for depth bounces
				rayTracing(color, viewpt, raypt, depth);

				// send vertex data to vertex shader through uniform
		 		FloatBuffer cBuf = Buffers.newDirectFloatBuffer(raypt);
				int colorLoc = gl.glGetUniformLocation(vfPrograms,  "rPoint"); 
				gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
				
				// send color data to fragment shader through uniform
		 		 cBuf = Buffers.newDirectFloatBuffer(color);
				 colorLoc = gl.glGetUniformLocation(vfPrograms,  "rColor"); 
				gl.glProgramUniform3fv(vfPrograms,  colorLoc, 1, cBuf);
				
				//draw the point
				gl.glDrawArrays(GL_POINTS, 0, 1);	
			}
		}
	}
	
	
	
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {

	    WIDTH = w/2; HEIGHT = h;
	 
		System.out.println("b) reshape: accommodate two viewports for comparason"); 
	    //projection is carried on the projection matrix
	    myLoadIdentity();
	    myOrtho(-WIDTH / 2, WIDTH / 2, -HEIGHT / 2, HEIGHT / 2, -4 * HEIGHT, 4 * HEIGHT); 		    
}


	public static void main(String[] args) {
		JOGL3_13_SrayTracing f = new JOGL3_13_SrayTracing();
		
		f.setSize(WIDTH*2, HEIGHT);
	}
}
